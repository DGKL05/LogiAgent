package com.logiagent.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.logiagent.api.dto.AuthUserDTO;
import com.logiagent.api.request.LoginRequest;
import com.logiagent.api.request.RegisterRequest;
import com.logiagent.api.response.LoginResponse;
import com.logiagent.api.response.TokenValidateResponse;
import com.logiagent.auth.entity.PermissionEntity;
import com.logiagent.auth.entity.RoleEntity;
import com.logiagent.auth.entity.UserEntity;
import com.logiagent.auth.entity.UserRoleEntity;
import com.logiagent.auth.mapper.PermissionMapper;
import com.logiagent.auth.mapper.RoleMapper;
import com.logiagent.auth.mapper.RolePermissionMapper;
import com.logiagent.auth.mapper.UserMapper;
import com.logiagent.auth.mapper.UserRoleMapper;
import com.logiagent.auth.service.AuthService;
import com.logiagent.common.auth.AuthConstant;
import com.logiagent.common.auth.JwtProperties;
import com.logiagent.common.auth.JwtUserContext;
import com.logiagent.common.auth.JwtUtils;
import com.logiagent.common.auth.RoleEnum;
import com.logiagent.common.auth.TokenAuthException;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private static final int ENABLED = 1;

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    public AuthServiceImpl(UserMapper userMapper,
                           RoleMapper roleMapper,
                           UserRoleMapper userRoleMapper,
                           PermissionMapper permissionMapper,
                           RolePermissionMapper rolePermissionMapper,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtProperties jwtProperties) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.permissionMapper = permissionMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "username and password are required");
        }
        UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, request.getUsername()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "username or password is incorrect");
        }
        if (!Integer.valueOf(ENABLED).equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "user is disabled");
        }

        List<String> roles = getRoleCodes(user.getId());
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), roles, jwtProperties);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtProperties.getExpireSeconds());
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRoles(roles);
        return response;
    }

    @Override
    public AuthUserDTO register(RegisterRequest request) {
        if (request == null || !StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "username and password are required");
        }
        Long existingCount = userMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, request.getUsername()));
        if (existingCount != null && existingCount > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "username already exists");
        }

        LocalDateTime now = LocalDateTime.now();
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setStatus(ENABLED);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        userMapper.insert(user);

        RoleEntity userRole = roleMapper.selectOne(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getRoleCode, RoleEnum.USER.name()));
        if (userRole != null) {
            UserRoleEntity relation = new UserRoleEntity();
            relation.setUserId(user.getId());
            relation.setRoleId(userRole.getId());
            relation.setCreateTime(now);
            userRoleMapper.insert(relation);
        }
        return toDTO(user);
    }

    @Override
    public AuthUserDTO me(String authorization) {
        JwtUserContext context = JwtUtils.parseToken(extractToken(authorization), jwtProperties);
        UserEntity user = userMapper.selectById(context.getUserId());
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "user not found");
        }
        if (!Integer.valueOf(ENABLED).equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "user is disabled");
        }
        return toDTO(user);
    }

    @Override
    public TokenValidateResponse validate(String authorization) {
        TokenValidateResponse response = new TokenValidateResponse();
        try {
            JwtUserContext context = JwtUtils.parseToken(extractToken(authorization), jwtProperties);
            response.setValid(true);
            response.setUserId(context.getUserId());
            response.setUsername(context.getUsername());
            response.setRoles(context.getRoles());
            response.setExpireAt(context.getExpireAt());
        } catch (TokenAuthException ex) {
            response.setValid(false);
        }
        return response;
    }

    private AuthUserDTO toDTO(UserEntity user) {
        AuthUserDTO dto = new AuthUserDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        dto.setRoles(getRoleCodes(user.getId()));
        dto.setPermissions(getPermissionCodes(user.getId()));
        return dto;
    }

    private List<String> getRoleCodes(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        return roleMapper.selectBatchIds(roleIds).stream()
                .map(RoleEntity::getRoleCode)
                .filter(StringUtils::hasText)
                .toList();
    }

    private List<String> getPermissionCodes(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> permissionIds = rolePermissionMapper.selectPermissionIdsByRoleIds(roleIds);
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new ArrayList<>();
        }
        return permissionMapper.selectBatchIds(permissionIds).stream()
                .map(PermissionEntity::getPermissionCode)
                .filter(StringUtils::hasText)
                .toList();
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "missing Authorization header");
        }
        if (authorization.startsWith(AuthConstant.BEARER)) {
            return authorization.substring(AuthConstant.BEARER.length());
        }
        return authorization;
    }
}

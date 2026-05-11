package com.logiagent.auth.service.impl;

import com.logiagent.api.request.LoginRequest;
import com.logiagent.api.response.LoginResponse;
import com.logiagent.auth.entity.RoleEntity;
import com.logiagent.auth.entity.UserEntity;
import com.logiagent.auth.mapper.PermissionMapper;
import com.logiagent.auth.mapper.RoleMapper;
import com.logiagent.auth.mapper.RolePermissionMapper;
import com.logiagent.auth.mapper.UserMapper;
import com.logiagent.auth.mapper.UserRoleMapper;
import com.logiagent.common.auth.JwtProperties;
import com.logiagent.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    private final UserMapper userMapper = mock(UserMapper.class);
    private final RoleMapper roleMapper = mock(RoleMapper.class);
    private final UserRoleMapper userRoleMapper = mock(UserRoleMapper.class);
    private final PermissionMapper permissionMapper = mock(PermissionMapper.class);
    private final RolePermissionMapper rolePermissionMapper = mock(RolePermissionMapper.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtProperties jwtProperties = new JwtProperties("test-secret", 7200L, "logiagent");

    @Test
    void loginShouldIssueAdminToken() {
        UserEntity user = buildUser(1);
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setRoleCode("ADMIN");
        when(userMapper.selectOne(any())).thenReturn(user);
        when(userRoleMapper.selectRoleIdsByUserId(1L)).thenReturn(List.of(1L));
        when(roleMapper.selectBatchIds(List.of(1L))).thenReturn(List.of(role));

        AuthServiceImpl authService = newAuthService();
        LoginResponse response = authService.login(loginRequest());

        assertEquals(1L, response.getUserId());
        assertEquals("admin", response.getUsername());
        assertEquals(List.of("ADMIN"), response.getRoles());
        assertTrue(response.getToken().split("\\.").length == 3);
    }

    @Test
    void disabledUserShouldNotLogin() {
        UserEntity user = buildUser(0);
        when(userMapper.selectOne(any())).thenReturn(user);

        AuthServiceImpl authService = newAuthService();

        assertThrows(BusinessException.class, () -> authService.login(loginRequest()));
    }

    private AuthServiceImpl newAuthService() {
        return new AuthServiceImpl(
                userMapper,
                roleMapper,
                userRoleMapper,
                permissionMapper,
                rolePermissionMapper,
                passwordEncoder,
                jwtProperties
        );
    }

    private LoginRequest loginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123456");
        return request;
    }

    private UserEntity buildUser(Integer status) {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin123456"));
        user.setStatus(status);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }
}

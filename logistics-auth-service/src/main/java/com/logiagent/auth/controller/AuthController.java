package com.logiagent.auth.controller;

import com.logiagent.api.dto.AuthUserDTO;
import com.logiagent.api.request.LoginRequest;
import com.logiagent.api.request.RegisterRequest;
import com.logiagent.api.response.LoginResponse;
import com.logiagent.api.response.TokenValidateResponse;
import com.logiagent.auth.service.AuthService;
import com.logiagent.common.auth.AuthConstant;
import com.logiagent.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"", "/api/auth"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/register")
    public Result<AuthUserDTO> register(@RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @GetMapping("/me")
    public Result<AuthUserDTO> me(@RequestHeader(AuthConstant.AUTHORIZATION) String authorization) {
        return Result.success(authService.me(authorization));
    }

    @PostMapping("/validate")
    public Result<TokenValidateResponse> validate(@RequestHeader(AuthConstant.AUTHORIZATION) String authorization) {
        return Result.success(authService.validate(authorization));
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("success");
    }
}

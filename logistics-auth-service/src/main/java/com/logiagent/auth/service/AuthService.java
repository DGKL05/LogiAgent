package com.logiagent.auth.service;

import com.logiagent.api.dto.AuthUserDTO;
import com.logiagent.api.request.LoginRequest;
import com.logiagent.api.request.RegisterRequest;
import com.logiagent.api.response.LoginResponse;
import com.logiagent.api.response.TokenValidateResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    AuthUserDTO register(RegisterRequest request);

    AuthUserDTO me(String authorization);

    TokenValidateResponse validate(String authorization);
}

package com.logiagent.common.auth;

public class TokenExpiredException extends TokenAuthException {

    public TokenExpiredException(String message) {
        super(message);
    }
}

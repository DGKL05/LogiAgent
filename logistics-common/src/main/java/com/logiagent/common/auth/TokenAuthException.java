package com.logiagent.common.auth;

public class TokenAuthException extends RuntimeException {

    public TokenAuthException(String message) {
        super(message);
    }

    public TokenAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}

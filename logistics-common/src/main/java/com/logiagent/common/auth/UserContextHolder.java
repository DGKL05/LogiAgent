package com.logiagent.common.auth;

public final class UserContextHolder {

    private static final ThreadLocal<JwtUserContext> USER_CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void set(JwtUserContext context) {
        USER_CONTEXT.set(context);
    }

    public static JwtUserContext get() {
        return USER_CONTEXT.get();
    }

    public static void clear() {
        USER_CONTEXT.remove();
    }
}

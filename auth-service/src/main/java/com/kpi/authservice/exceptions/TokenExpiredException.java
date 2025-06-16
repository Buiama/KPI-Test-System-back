package com.kpi.authservice.exceptions;

public class TokenExpiredException extends IllegalStateException {
    private static final String message = "Confirmation email token expired";
    public TokenExpiredException() {
        super(message);
    }
    public TokenExpiredException(String message) {
        super(message);
    }
}

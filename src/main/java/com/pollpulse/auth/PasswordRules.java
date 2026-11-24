package com.pollpulse.auth;

public final class PasswordRules {
    public static final String REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";
    public static final String MESSAGE = "minimum 8 characters with an uppercase letter, a lowercase letter, a digit and a special character";

    private PasswordRules() {
    }
}

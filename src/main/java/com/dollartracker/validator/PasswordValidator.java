package com.dollartracker.validator;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {
    private static final int MIN_LENGTH = 8;
    private static final String SPECIAL_CHARS_REGEX = ".*[!@#$%^&*].*";
    private static final String DIGITS_REGEX = "\\d+";

    public PasswordValidationResult validatePassword(String password) {
        // Check length
        if (password == null || password.length() < MIN_LENGTH) {
            return new PasswordValidationResult(false, "Password must be minimum 8 characters");
        }

        // Check for at least one special character
        if (!Pattern.matches(SPECIAL_CHARS_REGEX, password)) {
            return new PasswordValidationResult(false, "Password must contain at least one special character (!@#$%^&*)");
        }

        // Check if all characters are the same
        if (isAllSameCharacters(password)) {
            return new PasswordValidationResult(false, "Password cannot have all same characters");
        }

        // Check for max 2 consecutive numbers
        if (hasMoreThanTwoConsecutiveDigits(password)) {
            return new PasswordValidationResult(false, "Password cannot have more than 2 consecutive numbers");
        }

        // Check if alphanumeric
        if (!password.matches("^[a-zA-Z0-9!@#$%^&*]*$")) {
            return new PasswordValidationResult(false, "Password can only contain letters, numbers, and special characters");
        }

        return new PasswordValidationResult(true, "Password is valid");
    }

    private boolean isAllSameCharacters(String password) {
        if (password.length() < 1) return false;
        char firstChar = password.charAt(0);
        return password.chars().allMatch(c -> c == firstChar);
    }

    private boolean hasMoreThanTwoConsecutiveDigits(String password) {
        Pattern consecutiveDigits = Pattern.compile("\\d{3,}");
        return consecutiveDigits.matcher(password).find();
    }

    public static class PasswordValidationResult {
        private final boolean valid;
        private final String message;

        public PasswordValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}

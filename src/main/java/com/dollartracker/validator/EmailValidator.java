package com.dollartracker.validator;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class EmailValidator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9][A-Za-z0-9.]*@[A-Za-z0-9][A-Za-z0-9.]*\\.[A-Za-z]{2,}$";
    private static final int MAX_EMAIL_LENGTH = 75;

    public boolean isValidEmail(String email) {
        if (email == null || email.length() > MAX_EMAIL_LENGTH) {
            return false;
        }
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        return pattern.matcher(email).matches();
    }
}

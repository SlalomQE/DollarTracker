package com.dollartracker.service;

import com.dollartracker.dto.UserRegistrationDto;
import com.dollartracker.entity.User;
import com.dollartracker.repository.UserRepository;
import com.dollartracker.validator.EmailValidator;
import com.dollartracker.validator.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordValidator passwordValidator;

    @Autowired
    private EmailValidator emailValidator;

    public User registerUser(UserRegistrationDto registrationDto) throws Exception {
        // Validate first name
        if (registrationDto.getFirstName() == null || registrationDto.getFirstName().trim().isEmpty()) {
            throw new Exception("First Name is required");
        }
        if (registrationDto.getFirstName().length() > 50) {
            throw new Exception("First Name must be max 50 characters");
        }
        if (!registrationDto.getFirstName().matches("^[a-zA-Z\\s]+$")) {
            throw new Exception("First Name can only contain alphabets");
        }

        // Validate last name
        if (registrationDto.getLastName() == null || registrationDto.getLastName().trim().isEmpty()) {
            throw new Exception("Last Name is required");
        }
        if (registrationDto.getLastName().length() > 50) {
            throw new Exception("Last Name must be max 50 characters");
        }
        if (!registrationDto.getLastName().matches("^[a-zA-Z\\s]+$")) {
            throw new Exception("Last Name can only contain alphabets");
        }

        // Validate email
        if (registrationDto.getEmail() == null || registrationDto.getEmail().trim().isEmpty()) {
            throw new Exception("Email id is required");
        }
        if (!emailValidator.isValidEmail(registrationDto.getEmail())) {
            throw new Exception("Invalid email format");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new Exception("Email id exists, please log in");
        }

        // Validate password
        if (registrationDto.getPassword() == null || registrationDto.getPassword().isEmpty()) {
            throw new Exception("Password is required");
        }
        if (registrationDto.getConfirmPassword() == null || registrationDto.getConfirmPassword().isEmpty()) {
            throw new Exception("Confirm Password is required");
        }
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new Exception("Passwords do not match");
        }

        PasswordValidator.PasswordValidationResult passwordValidation = passwordValidator.validatePassword(registrationDto.getPassword());
        if (!passwordValidation.isValid()) {
            throw new Exception(passwordValidation.getMessage());
        }

        // Create user
        User user = new User();
        user.setFirstName(registrationDto.getFirstName().trim());
        user.setLastName(registrationDto.getLastName().trim());
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setIsActive(true);

        return userRepository.save(user);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void resetPassword(String email, String newPassword, String confirmPassword) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email is required");
        }
        if (!emailValidator.isValidEmail(email)) {
            throw new Exception("Invalid email format");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new Exception("Email not found in system");
        }

        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            throw new Exception("New password and confirm password are required");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new Exception("Passwords do not match");
        }

        PasswordValidator.PasswordValidationResult passwordValidation = passwordValidator.validatePassword(newPassword);
        if (!passwordValidation.isValid()) {
            throw new Exception(passwordValidation.getMessage());
        }

        User user = userOptional.get();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

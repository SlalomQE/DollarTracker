package com.dollartracker.controller;

import com.dollartracker.dto.ApiResponse;
import com.dollartracker.dto.LoginDto;
import com.dollartracker.dto.ResetPasswordDto;
import com.dollartracker.dto.UserRegistrationDto;
import com.dollartracker.entity.User;
import com.dollartracker.security.JwtTokenProvider;
import com.dollartracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody UserRegistrationDto registrationDto, HttpServletResponse response) {
        try {
            User user = userService.registerUser(registrationDto);
            String token = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());
            
            // Set token in httpOnly cookie
            Cookie cookie = new Cookie("authToken", token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(86400); // 24 hours
            cookie.setPath("/");
            response.addCookie(cookie);

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .data(user)
                    .message("User registered successfully")
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();

            if (e.getMessage().contains("Email id exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        try {
            Optional<User> userOptional = userService.findUserByEmail(loginDto.getUsername());
            if (!userOptional.isPresent()) {
                ApiResponse apiResponse = ApiResponse.builder()
                        .success(false)
                        .error("Invalid username or password")
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
            }

            User user = userOptional.get();
            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPasswordHash())) {
                ApiResponse apiResponse = ApiResponse.builder()
                        .success(false)
                        .error("Invalid username or password")
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
            }

            String token = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());

            // Set token in httpOnly cookie
            Cookie cookie = new Cookie("authToken", token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(86400); // 24 hours
            cookie.setPath("/");
            response.addCookie(cookie);

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .data(user)
                    .message("Login successful")
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();

            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        try {
            userService.resetPassword(resetPasswordDto.getEmail(), resetPasswordDto.getNewPassword(), resetPasswordDto.getConfirmPassword());
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .message("Password reset successfully")
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();

            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("authToken", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        ApiResponse apiResponse = ApiResponse.builder()
                .success(true)
                .message("Logout successful")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}

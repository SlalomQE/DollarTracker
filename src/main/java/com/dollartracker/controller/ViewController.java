package com.dollartracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "reset-password";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}

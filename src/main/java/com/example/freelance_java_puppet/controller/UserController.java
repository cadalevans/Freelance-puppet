package com.example.freelance_java_puppet.controller;

import com.example.freelance_java_puppet.entity.User;
import com.example.freelance_java_puppet.ressource.LoginRequest;
import com.example.freelance_java_puppet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1️⃣ User Registration
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully! Check your email for verification.");
    }

    // 2️⃣ Resend Verification Email
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam String email) {
        userService.resendVerificationEmail(email);
        return ResponseEntity.ok("Verification email resent successfully!");
    }

    // 3️⃣ Verify Email
    @PostMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String email, @RequestParam String code) {
        boolean verified = userService.verifyUser(email, code);
        if (verified) {
            return ResponseEntity.ok("Email verified successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification code!");
        }
    }

    // 4️⃣ Request Password Reset (User forgets password)
    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        userService.sendPasswordResetCode(email);
        return ResponseEntity.ok("Password reset code sent to your email.");
    }

    // 5️⃣ Resend Password Reset Email
    @PostMapping("/resend-password-reset")
    public ResponseEntity<String> resendPasswordResetEmail(@RequestParam String email) {
        userService.resendPasswordResetEmail(email);
        return ResponseEntity.ok("Password reset email resent successfully!");
    }

    // 6️⃣ Reset Password (After receiving reset code)
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String code, @RequestParam String newPassword) {
        boolean resetSuccessful = userService.resetPassword(email, code, newPassword);
        if (resetSuccessful) {
            return ResponseEntity.ok("Password reset successful! You can now log in with your new password.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired reset code!");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        User user = userService.getUserByEmail(loginRequest.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // Successful authentication
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}

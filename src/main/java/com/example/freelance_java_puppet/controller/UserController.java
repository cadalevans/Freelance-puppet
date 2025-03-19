package com.example.freelance_java_puppet.controller;

import com.example.freelance_java_puppet.DTO.HistoryDTO;
import com.example.freelance_java_puppet.entity.User;
import com.example.freelance_java_puppet.ressource.LoginRequest;
import com.example.freelance_java_puppet.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@CrossOrigin("**")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1️⃣ User Registration
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully! Check your email for verification.");
    }

    @GetMapping("/is-verified/{email}")
    public ResponseEntity<Boolean> checkIfUserIsVerified(@PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
        return ResponseEntity.ok(user.isVerified());
    }

    // 1️⃣ Resend Verification Email
    @PostMapping("/resend-verification/{email}")
    public ResponseEntity<?> resendVerificationEmail(@PathVariable String email) {
        userService.resendVerificationEmail(email);
        return ResponseEntity.ok(Collections.singletonMap("message", "Email verification has been send "));
    }

    // 2️⃣ Verify Email
    @PostMapping("/verify/{email}/{code}")
    public ResponseEntity<?> verifyUser(@PathVariable String email, @PathVariable String code) {
        boolean verified = userService.verifyUser(email, code);
        if (verified) {
            return ResponseEntity.ok(Collections.singletonMap("message","Email verified successfully"));
        } else {
            Map<String,String> response = new HashMap<>();
            response.put("message", "verification error please check the code ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 3️⃣ Request Password Reset (User forgets password)
    @PostMapping("/forgot-password/{email}")
    public ResponseEntity<?> requestPasswordReset(@PathVariable String email) {
        userService.sendPasswordResetCode(email);
        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset email has been send to your email"));
    }

    // 4️⃣ Resend Password Reset Email
    @PostMapping("/resend-password-reset/{email}")
    public ResponseEntity<?> resendPasswordResetEmail(@PathVariable String email) {
        userService.resendPasswordResetEmail(email);
        return ResponseEntity.ok( Collections.singletonMap("message", "Password reset email resent successfully!"));
    }

    // 5️⃣ Reset Password (After receiving reset code)
    @PostMapping("/reset-password/{email}/{code}")
    public ResponseEntity<?> resetPassword(@PathVariable String email,
                                                @PathVariable String code,
                                                @RequestParam String newPassword) {
        boolean resetSuccessful = userService.resetPassword(email, code, newPassword);
        if (resetSuccessful) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Password reset successful! You can now log in with your new password."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "Invalid or expired reset code!"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        User user = userService.getUserByEmail(loginRequest.getEmail());

        if (user == null) {

           // log.error("User not found with email: " + loginRequest.getEmail());
            Map<String,String> response = new HashMap<>();
            response.put("message","Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if(!user.isVerified()){

        //    log.error("User with email " + loginRequest.getEmail() + " is not verified.");
            Map<String,String> response = new HashMap<>();
            response.put("message", "You have to verify your account ");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // Successful authentication
            Map<String,String> response = new HashMap<>();
            response.put("message","Login successful");
            return ResponseEntity.ok(response);
        } else {
            Map<String,String> response = new HashMap<>();
            response.put("message","Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/{userId}/histories")
    public ResponseEntity<List<HistoryDTO>> getHistoriesByUser(@PathVariable("userId") int userId) {
        List<HistoryDTO> histories = userService.getHistoriesByUser(userId);
        return ResponseEntity.ok(histories);
    }

    @GetMapping("/id-by-email/{email}")
    public ResponseEntity<?> getUserIdByEmail( @PathVariable("email") String email){
      int userId =  userService.findUserIdByEmail(email);

         return ResponseEntity.ok(userId);
    }


    // delete all history by user id

    @DeleteMapping("/{userId}/histories")
    public ResponseEntity<String> deleteAllHistories(@PathVariable int userId) {
        try {
            userService.deleteHistoryByUserId(userId);
            return ResponseEntity.ok("All histories deleted for user with id " + userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

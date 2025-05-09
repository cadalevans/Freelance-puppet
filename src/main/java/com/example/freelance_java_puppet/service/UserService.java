package com.example.freelance_java_puppet.service;

import com.example.freelance_java_puppet.DTO.HistoryDTO;
import com.example.freelance_java_puppet.entity.Category;
import com.example.freelance_java_puppet.entity.History;
import com.example.freelance_java_puppet.entity.Role;
import com.example.freelance_java_puppet.entity.User;
import com.example.freelance_java_puppet.repository.HistoryRepository;
import com.example.freelance_java_puppet.repository.UserRepository;
import com.example.freelance_java_puppet.ressource.EmailAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
    @Autowired
    private HistoryRepository historyRepository;

    private boolean emailExists(String email) {
        // Use the repository's findByEmail method to check if an entry with the given email exists
        User user = userRepository.findByEmail(email);
        return user != null; // If user is not null, the email exists
    }

    public User saveUser(User user){
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        // Check if the email already exists in the database
        if (emailExists(user.getEmail())) {
            // Handle the case where the email already exists, e.g., throw an exception or return an error response
            throw new EmailAlreadyExistsException("Email address already exists");
        }

        String verificationCode = generateResetToken();
        user.setCode(verificationCode);

        LocalDateTime codeExpirationDate = codeExpiryDate();

        user.setCodeExpiryDate(codeExpirationDate);
        user.setVerified(false);
        user.setRole(Collections.singleton(Role.ROLE_USER));


        User savedUser = userRepository.save(user);

        // Send a confirmation email
        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getCode());

        return savedUser;
    }

    private String generateResetToken() {
        // Generate a unique token, e.g., using UUID.randomUUID() or a random string generator
        return UUID.randomUUID().toString().substring(0,6);
    }

    private LocalDateTime codeExpiryDate(){
        return LocalDateTime.now().plusHours(2);
    }


    public boolean verifyUser(String email, String code) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found!");
        }

        if (user.isVerified()) {
            throw new RuntimeException("User already verified!");
        }

        if (user.getCodeExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code expired!");
        }

        if (!user.getCode().equals(code)) {
            throw new RuntimeException("Invalid verification code!");
        }

        user.setVerified(true);
        user.setCode(null);
        user.setCodeExpiryDate(null);
        userRepository.save(user);

        return true;
    }

    public String sendPasswordResetCode(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found!");
        }
        String verificationCode = generateResetToken();
        user.setCode(verificationCode);

        LocalDateTime codeExpirationDate = codeExpiryDate();

        user.setCodeExpiryDate(codeExpirationDate);
        user.setVerified(false);


        // Save the updated user
        userRepository.save(user);

        // Send password reset email
        emailService.sendPasswordResetEmail(user.getEmail(), user.getCode());
        return "A verification email ass be send to "+ user.getEmail();
    }

    public String resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found!");
        }
        // Check if user is already verified
        if (user.isVerified()) {
            throw new RuntimeException("User is already verified!");
        }
        // If the code is expired, generate a new one
        if (user.getCodeExpiryDate().isBefore(LocalDateTime.now())) {
            String verificationCode = generateResetToken();
            user.setCode(verificationCode);

            LocalDateTime codeExpirationDate = codeExpiryDate();

            user.setCodeExpiryDate(codeExpirationDate);
            userRepository.save(user);
        }

        // Send password reset email
        emailService.sendVerificationEmail(user.getEmail(), user.getCode());
        return "A verification email ass be send to "+ user.getEmail();
    }

    public String resendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found!");
        }
        // Check if user is already verified
        if (!user.isVerified()) {
            throw new RuntimeException("you have to verify this account before processing!");
        }
        // If the code is expired, generate a new one
        if (user.getCodeExpiryDate().isBefore(LocalDateTime.now())) {
            String verificationCode = generateResetToken();
            user.setCode(verificationCode);

            LocalDateTime codeExpirationDate = codeExpiryDate();

            user.setCodeExpiryDate(codeExpirationDate);
            userRepository.save(user);
        }

        // Send password reset email
        emailService.sendPasswordResetEmail(user.getEmail(), user.getCode());
        return "A verification email ass be send to "+ user.getEmail();
    }

    public boolean resetPassword(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found!");
        }


        // Check if the reset code is expired
        if (user.getCodeExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset code expired!");
        }

        // Check if the reset code is correct
        if (!user.getCode().equals(code)) {
            throw new RuntimeException("Invalid reset code!");
        }

        // Hash and set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setCode(null); // Clear reset code after use
        user.setCodeExpiryDate(null);

        userRepository.save(user);
        return true;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public int getIdByEmail(String email){
        User user = userRepository.findByEmail(email);
        return user != null? user.getId() : null;
    }

    public List<HistoryDTO> getHistoriesByUser(int userId) {
       User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found!"));

        List<HistoryDTO> historyDTOs = user.getHistories().stream()
                .map(history -> {
                    HistoryDTO historyDTO = new HistoryDTO();
                    historyDTO.setId(history.getId());
                    historyDTO.setName(history.getName());
                    historyDTO.setAudio(history.getAudio());
                    historyDTO.setImage(history.getImage());
                    historyDTO.setPrice(history.getPrice());
                    historyDTO.setDescription(history.getDescription());
                    return historyDTO;
                })
                .collect(Collectors.toList());

        return historyDTOs;
    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public int findUserIdByEmail(String email){
        User user = findByEmail(email);
       return user.getId();
    }

    public String deleteHistoryByUserId(int userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));
        List<History> history = user.getHistories();
        user.getHistories().clear();
        // Persist changes to remove the relationships (if needed)
        userRepository.save(user);  // Save the user after clearing histories

        // Delete all histories related to this user
        historyRepository.deleteByUsers_Id(userId);

        return "Successful deleted history from user "+ user.getLastName();

    }
}

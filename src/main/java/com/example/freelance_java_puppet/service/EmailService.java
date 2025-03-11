package com.example.freelance_java_puppet.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String code) {
        String subject = "Verify Your Email";
        String content = "<h3>Hello,</h3>"
                + "<p>Thank you for registering. Your verification code is:</p>"
                + "<h2 style='color:blue;'>" + code + "</h2>"
                + "<p>This code will expire in <b>1 hour</b>.</p>"
                + "<p>Best regards,<br>Your Team</p>";

        sendHtmlEmail(to, subject, content);
    }

    public void sendPasswordResetEmail(String to, String code) {
        String subject = "Password Reset Request";
        String content = "<h3>Reset Your Password</h3>"
                + "<p>You requested to reset your password. Use the code below to reset it:</p>"
                + "<h2 style='color:red;'>" + code + "</h2>"
                + "<p>If you didn’t request a password reset, please ignore this email.</p>"
                + "<p>Best regards,<br>Your Team</p>";

        sendHtmlEmail(to, subject, content);
    }

    public void sendInvoiceEmail(String to, String invoiceUrl) {
        String subject = "💳 Payment Receipt - Thank you for your purchase!";
        String content = """
            <div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 8px; padding: 20px; background-color: #f9f9f9;'>
                <div style='text-align: center;'>
                    <h2 style='color: #4CAF50;'>✅ Payment Successful</h2>
                    <p style='color: #777;'>Thank you for your payment. Your invoice is ready for download.</p>
                </div>
                <div style='text-align: center; margin: 20px 0;'>
                    <a href='""" + invoiceUrl + """
                       '
                       style='background-color: #4CAF50; color: white; padding: 12px 30px; text-decoration: none; font-size: 16px; border-radius: 5px;'>
                       📄 Download Invoice
                    </a>
                </div>
                <div style='margin-top: 30px; text-align: center; color: #999; font-size: 12px;'>
                    <p>If you have any questions about this payment, please contact our support team.</p>
                    <p>Best regards,<br><strong>Your Company Name</strong></p>
                </div>
            </div>
            """;

        sendHtmlEmail(to, subject, content);
    }


    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("mandoupam@gmail.com","No-Reply");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML format

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

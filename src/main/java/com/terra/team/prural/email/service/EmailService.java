package com.terra.team.prural.email.service;

import com.terra.team.prural.email.template.EmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    public void sendVerificationEmail(String toEmail, String token) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail, "Prural - Consignatario de haciendo");
        helper.setTo(toEmail);
        helper.setSubject("Verifica tu cuenta");
        
        String verificationUrl = baseUrl + "/api/auth/verify?token=" + token;
        String htmlContent = EmailTemplate.getVerificationEmailTemplate(verificationUrl);
        
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
    
    public void sendPasswordResetEmail(String toEmail, String token) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail, "Prural - Consignatario de haciendo");
        helper.setTo(toEmail);
        helper.setSubject("Restablecer contraseña");
        
        String resetUrl = baseUrl + "/api/auth/reset-password?token=" + token;
        String htmlContent = EmailTemplate.getPasswordResetEmailTemplate(resetUrl);
        
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}

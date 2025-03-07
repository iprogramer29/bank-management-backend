package com.bank.service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("your-email@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true); // HTML content

            mailSender.send(message);
            System.out.println("✅ Email sent successfully to " + toEmail);
        } catch (MessagingException e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
        }
    }
}

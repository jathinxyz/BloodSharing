package com.bloodmatcher.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@bloodmatcher.org}")
    private String mailFrom;

    @Value("${spring.mail.host:}")
    private String mailHost;

    public EmailNotificationService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email notification safely.
     * Returns true if sent successfully, false if SMTP is not configured or sending failed.
     * Never throws exceptions so that business transactions are never aborted by email issues.
     */
    public boolean sendEmail(String toEmail, String subject, String text) {
        if (mailSender == null || mailHost == null || mailHost.trim().isEmpty()) {
            log.info("Email notification skipped: SMTP host is not configured");
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(toEmail);
            message.setSubject("[Blood Matcher] " + subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Email notification sent successfully to {}", toEmail);
            return true;
        } catch (Exception e) {
            log.warn("Failed to deliver email notification to {}: {}", toEmail, e.getMessage());
            return false;
        }
    }
}

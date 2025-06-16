package com.kpi.emailservice.services.implementations;

import com.kpi.emailservice.exceptions.FailedToSendEmailException;
import com.kpi.emailservice.services.interfaces.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class EmailService implements IEmailService {
    private final JavaMailSender mailSender;

    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());
            helper.setText(email, true);
            helper.addInline("top", new ClassPathResource("images/top.png"));
            helper.addInline("bottom", new ClassPathResource("images/bottom.png"));
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("system@kpi.ua");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new FailedToSendEmailException();
        }
    }
}

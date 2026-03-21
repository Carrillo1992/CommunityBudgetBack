package com.communitybudget.modules.user.infrastructure.web;

import com.communitybudget.common.exceptions.exception.BadRequestException;
import com.communitybudget.modules.user.domain.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
public class MailNotifier implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final String mailFrom;

    public MailNotifier(final JavaMailSender mailSender,
                        final SpringTemplateEngine templateEngine,
                        @Value("${mail.from:noreply@communitybudget.local}") final String mailFrom) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.mailFrom = mailFrom;
    }

    @Override
    public void sendPasswordResetEmail(final String to, final String resetLink) {
        Context context = new Context();
        context.setVariable("userEmail", to);
        context.setVariable("resetLink", resetLink);
        context.setVariable("expirationMinutes", 15);

        String htmlBody = templateEngine.process("mail/password-reset", context);
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject("Recupera tu contrasena - CommunityBudget");
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new BadRequestException("No se pudo enviar el correo de recuperacion");
        }
    }
}

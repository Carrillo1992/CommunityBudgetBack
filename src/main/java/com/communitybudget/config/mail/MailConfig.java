package com.communitybudget.config.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender(@Value("${spring.mail.host}") final String host,
                                         @Value("${spring.mail.port}") final int port,
                                         @Value("${spring.mail.username:}") final String username,
                                         @Value("${spring.mail.password:}") final String password,
                                         @Value("${spring.mail.properties.mail.smtp.auth:false}") final boolean smtpAuth,
                                         @Value("${spring.mail.properties.mail.smtp.starttls.enable:false}") final boolean startTls) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", String.valueOf(smtpAuth));
        props.put("mail.smtp.starttls.enable", String.valueOf(startTls));

        return sender;
    }
}


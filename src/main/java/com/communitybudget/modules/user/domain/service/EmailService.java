package com.communitybudget.modules.user.domain.service;

public interface EmailService {

    void sendPasswordResetEmail(final String to, final String resetLink);

}

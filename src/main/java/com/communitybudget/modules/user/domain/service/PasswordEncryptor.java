package com.communitybudget.modules.user.domain.service;


public interface PasswordEncryptor {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);

}


package com.communitybudget.modules.user.infrastructure;

import com.communitybudget.modules.user.domain.repository.UserRepository;
import com.communitybudget.modules.user.domain.service.PasswordEncryptor;
import com.communitybudget.modules.user.domain.service.UserService;
import com.communitybudget.modules.user.domain.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserBeanConfig {

    @Bean
    public UserService userService(UserRepository userRepository, PasswordEncryptor passwordEncryptor) {
        return new UserServiceImpl(userRepository, passwordEncryptor);
    }

}

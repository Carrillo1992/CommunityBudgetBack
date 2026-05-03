package com.communitybudget.modules.group.infrastructure;

import com.communitybudget.modules.group.application.mapper.GroupMapper;
import com.communitybudget.modules.group.domain.service.GroupService;
import com.communitybudget.modules.group.domain.service.impl.GroupServiceImpl;
import com.communitybudget.modules.group.infrastructure.persistence.GroupReadRepository;
import com.communitybudget.modules.group.infrastructure.persistence.GroupRepositoryImpl;
import com.communitybudget.modules.group.infrastructure.persistence.common.JpaSpringGroupRepository;
import com.communitybudget.modules.user.domain.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupBeanConfig {

    @Bean
    public GroupService groupService(final GroupRepositoryImpl groupRepository, final UserService userService) {
        return new GroupServiceImpl(groupRepository, userService);
    }

}

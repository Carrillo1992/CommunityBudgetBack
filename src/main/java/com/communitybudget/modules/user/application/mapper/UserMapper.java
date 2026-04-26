package com.communitybudget.modules.user.application.mapper;

import com.communitybudget.modules.user.application.dto.UserCreateDTO;
import com.communitybudget.modules.user.application.dto.UserDTO;
import com.communitybudget.modules.user.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);


    UserDTO toDto(final User user);

    User fromDto(final UserDTO userDTO);

    UserDTO fromCreateDto(final UserCreateDTO userCreateDTO);

    UserCreateDTO toCreateDto(final User user);

}

package com.communitybudget.modules.user.application.mapper;

import com.communitybudget.application.dto.UserCreateDTO;
import com.communitybudget.application.dto.UserDTO;
import com.communitybudget.modules.user.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", expression = "java(user.getId() != null ? user.getId().toString() : null)")
    @Mapping(target = "avatarUrl", ignore = true)
    UserDTO toDto(final User user);

    @Mapping(target = "id", expression = "java(userDTO.getId() != null ? Long.parseLong(userDTO.getId()) : null)")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User fromDto(final UserDTO userDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User fromCreateDto(final UserCreateDTO userCreateDTO);

    @Mapping(target = "avatarUrl", ignore = true)
    UserCreateDTO toCreateDto(final User user);

}

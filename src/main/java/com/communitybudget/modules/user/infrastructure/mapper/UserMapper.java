package com.communitybudget.modules.user.infrastructure.mapper;

import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity toEntity(User user);

    User toDomain(UserEntity userEntity);


}

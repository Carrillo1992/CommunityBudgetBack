package com.upgrade.communitybudget.infrastructure.mapper;

import com.upgrade.communitybudget.domain.model.User;
import com.upgrade.communitybudget.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity toEntity(User user);

    User toDomain(UserEntity userEntity);


}

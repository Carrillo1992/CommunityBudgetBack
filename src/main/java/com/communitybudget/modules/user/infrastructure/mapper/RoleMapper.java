package com.communitybudget.modules.user.infrastructure.mapper;

import com.communitybudget.modules.user.domain.model.Role;
import com.communitybudget.modules.user.infrastructure.persistence.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);


    Role toDomain(RoleEntity roleEntity);
}

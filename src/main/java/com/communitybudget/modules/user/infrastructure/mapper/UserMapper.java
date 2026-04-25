package com.communitybudget.modules.user.infrastructure.mapper;

import com.communitybudget.modules.user.domain.model.Role;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.infrastructure.persistence.entity.RoleEntity;
import com.communitybudget.modules.user.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToEntities")

    UserEntity toEntity(User user);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "entitiesToRoles")
    User toDomain(UserEntity userEntity);


    @Named("rolesToEntities")
    default Set<RoleEntity> rolesToEntities(Set<Role> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(role -> {
                    RoleEntity entity = new RoleEntity();
                    entity.setId(role.getId());
                    entity.setName(role.getName());
                    return entity;
                })
                .collect(Collectors.toSet());
    }

    @Named("entitiesToRoles")
    default Set<Role> entitiesToRoles(Set<RoleEntity> roleEntities) {
        if (roleEntities == null) {
            return Collections.emptySet();
        }
        return roleEntities.stream()
                .map(entity -> new Role(entity.getId(), entity.getName()))
                .collect(Collectors.toSet());
    }
}

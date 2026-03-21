package com.communitybudget.modules.user.infrastructure.mapper;

import com.communitybudget.modules.user.domain.valueobjects.PasswordRecovery;
import com.communitybudget.modules.user.infrastructure.persistence.entity.PasswordResetEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PasswordResetMapper {

    PasswordResetMapper INSTANCE = Mappers.getMapper(PasswordResetMapper.class);

    PasswordRecovery toDomain(final PasswordResetEntity entity);

    PasswordResetEntity toPersistence(final PasswordRecovery passwordRecovery);
}

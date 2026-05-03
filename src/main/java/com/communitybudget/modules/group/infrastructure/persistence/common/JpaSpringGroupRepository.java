package com.communitybudget.modules.group.infrastructure.persistence.common;

import com.communitybudget.modules.group.infrastructure.persistence.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaSpringGroupRepository extends JpaRepository<GroupEntity, Long> {


    @Query("SELECT g FROM GroupEntity g JOIN g.members m WHERE m.user.id = :userId")
    List<GroupEntity> findGroupsWhereUserIsMember(@Param("userId") final Long userId);

    Optional<GroupEntity> findByInviteCode(final String inviteCode);
}

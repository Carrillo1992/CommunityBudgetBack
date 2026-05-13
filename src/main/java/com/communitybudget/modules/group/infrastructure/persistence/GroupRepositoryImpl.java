package com.communitybudget.modules.group.infrastructure.persistence;

import com.communitybudget.common.exceptions.exception.GroupNotFoundException;
import com.communitybudget.modules.group.application.mapper.GroupMapper;
import com.communitybudget.modules.group.domain.model.Group;
import com.communitybudget.modules.group.domain.repository.GroupRepository;
import com.communitybudget.modules.group.infrastructure.persistence.common.JpaSpringGroupRepository;
import com.communitybudget.modules.group.infrastructure.persistence.entity.GroupEntity;
import org.springframework.stereotype.Repository;

@Repository
public class GroupRepositoryImpl implements GroupRepository {

    private final JpaSpringGroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public GroupRepositoryImpl(JpaSpringGroupRepository groupRepository, GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
    }

    @Override
    public Group save(final Group group) {

        GroupEntity groupEntity = groupMapper.groupToEntity(group);

        GroupEntity savedEntity = groupRepository.save(groupEntity);

        return groupMapper.entityToGroup(savedEntity);
    }

    @Override
    public Group findById(final Long groupId) {
        return groupRepository.findById(groupId).map(groupMapper::entityToGroup)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));
    }

    @Override
    public Group findByInviteCode(final String inviteCode) {
        return groupRepository.findByInviteCode(inviteCode).map(groupMapper::entityToGroup)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with invite code: " + inviteCode));
    }

    @Override
    public Boolean existsById(Long groupId) {
        return groupRepository.existsById(groupId);
    }

    @Override
    public void delete(final Group group) {
        groupRepository.delete(groupMapper.groupToEntity(group));
    }

}

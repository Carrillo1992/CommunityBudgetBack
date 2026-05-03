package com.communitybudget.modules.group.infrastructure.persistence;

import com.communitybudget.modules.group.application.mapper.GroupMapper;
import com.communitybudget.modules.group.infrastructure.persistence.common.JpaSpringGroupRepository;
import com.communitybudget.modules.group.infrastructure.persistence.entity.GroupEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupReadRepository {

    private final JpaSpringGroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public GroupReadRepository(JpaSpringGroupRepository groupRepository, GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
    }

        public GroupEntity findById(Long groupId) {
            return groupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        }

        public List<GroupEntity> findAllByUserId(Long userId) {
            return groupRepository.findGroupsWhereUserIsMember(userId);
        }

        public List<GroupEntity> findAll() {
            return groupRepository.findAll();
        }
}

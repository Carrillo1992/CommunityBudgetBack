package com.communitybudget.modules.group.application.service;

import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.group.application.dto.*;
import com.communitybudget.modules.group.application.mapper.GroupMapper;
import com.communitybudget.modules.group.domain.model.Group;
import com.communitybudget.modules.group.domain.service.GroupService;
import com.communitybudget.modules.group.infrastructure.persistence.GroupReadRepository;
import com.communitybudget.modules.group.infrastructure.persistence.entity.GroupEntity;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupApplicationService {

    private final GroupService groupService;
    private final GroupMapper groupMapper;
    private final UserRepository userRepository;
    private final GroupReadRepository groupReadRepository;

    public GroupApplicationService(final GroupService groupService, GroupMapper groupMapper, UserRepository userRepository, GroupReadRepository groupReadRepository) {
        this.groupService = groupService;
        this.groupMapper = groupMapper;
        this.userRepository = userRepository;
        this.groupReadRepository = groupReadRepository;
    }

    @Transactional
    public GroupDto createGroup(final CreateGroupRequest request, final Long userId) {
        Group createdGroup = groupService.createGroup(groupMapper.createGroupDtoToGroup(request, userId));
        User createUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return groupMapper.createdGroupToGroupDto(createdGroup, createUser);
    }

    @Transactional(readOnly = true)
    public GroupDto getGroupById(final Long groupId) {
        GroupEntity group = groupReadRepository.findById(groupId);
        return groupMapper.groupEntityToGroupDto(group);
    }

    @Transactional(readOnly = true)
    public List<GroupDto> getMyGroups(final Long userId) {
        List<GroupEntity> groups = groupReadRepository.findAllByUserId(userId);
        return groupMapper.groupEntitiesToGroupDtos(groups);
    }

    @Transactional(readOnly = true)
    public List<GroupDto> getAllGroups() {
        return groupMapper.groupEntitiesToGroupDtos(groupReadRepository.findAll());
    }

    @Transactional
    public void deleteGroup(final Long groupId, final Long userId) {
        groupService.deleteGroup(groupId, userId);
    }

    @Transactional
    public GroupDto joinGroup(final String inviteCode, final Long userId) {
        Group userGroup = groupService.joinGroup(inviteCode, userId);
        GroupEntity group = groupReadRepository.findById(userGroup.getId());
        return groupMapper.groupEntityToGroupDto(group);
    }

    @Transactional
    public GroupDto joinGroup(final String inviteCode, final Long userId, final Long guestId) {
        Group userGroup = groupService.joinGroup(inviteCode, userId, guestId);
        GroupEntity group = groupReadRepository.findById(userGroup.getId());
        return groupMapper.groupEntityToGroupDto(group);
    }

    @Transactional
    public void kickUserFromGroup(final Long groupId, final Long userId, final Long requesterId) {
        groupService.kickGroup(groupId, userId, requesterId);
    }

    @Transactional
    public void leaveGroup(final Long groupId, final Long userId) {
        groupService.leaveGroup(groupId, userId);
    }

    @Transactional
    public GroupDto addGuestMember(final Long groupId, final GuestMemberDto guestMemberRequest) {
        groupService.joinGuestToGroup(groupId, guestMemberRequest.getName());
        GroupEntity group = groupReadRepository.findById(groupId);
        return groupMapper.groupEntityToGroupDto(group);
    }

    @Transactional
    public GroupDto updateGroup(final Long groupId, final UpdateGroupDto updateRequest, final Long requesterId) {
        Group groupToUpdate = groupMapper.UpdateGroupDtoToGroup(updateRequest, groupId);
        groupService.updateGroup(groupToUpdate, requesterId);
        GroupEntity updatedGroup = groupReadRepository.findById(groupId);
        if (updatedGroup == null) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }
        return groupMapper.groupEntityToGroupDto(updatedGroup);
    }

    @Transactional
    public InviteCodeResponse updateInviteCode(final Long groupId, final Long requesterId) {
        groupService.updateGroupInviteCode(groupId, requesterId);
        GroupEntity updatedGroup = groupReadRepository.findById(groupId);
        if (updatedGroup == null) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }
        InviteCodeResponse response = new InviteCodeResponse();
        response.setInviteCode(updatedGroup.getInviteCode());
        return response;
    }

    public GroupDto promoteToAdmin(final Long groupId, final Long userId, final Long id) {
        groupService.promoteToAdmin(groupId, userId, id);
        GroupEntity updatedGroup = groupReadRepository.findById(groupId);
        if (updatedGroup == null) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }
        return groupMapper.groupEntityToGroupDto(updatedGroup);
    }
}

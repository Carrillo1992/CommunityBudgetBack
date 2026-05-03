package com.communitybudget.modules.group.domain.service.impl;

import com.communitybudget.common.exceptions.exception.GroupNotFoundException;
import com.communitybudget.common.exceptions.exception.GroupRequestException;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.common.utils.StringUtils;
import com.communitybudget.modules.group.domain.model.Group;
import com.communitybudget.modules.group.domain.repository.GroupRepository;
import com.communitybudget.modules.group.domain.service.GroupService;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.service.UserService;

import java.time.LocalDateTime;

public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserService userService;

    public GroupServiceImpl(GroupRepository groupRepository, UserService userService) {
        this.groupRepository = groupRepository;
        this.userService = userService;
    }


    @Override
    public Group createGroup(final Group group) {
        if (group.getOwnerId() == null) {
            throw new GroupRequestException("Group owner ID cannot be null");
        }
        group.addMember(group.getOwnerId(), Boolean.TRUE);
        
        generateUniqueInviteCode(group);
        
        group.setCreatedAt(LocalDateTime.now());
        return groupRepository.save(group);
    }

    @Override
    public void deleteGroup(final Long groupId, final Long requesterId) {
        Group group = getGroupOrThrow(groupId);
        if (!group.getOwnerId().equals(requesterId)) {
            throw new GroupRequestException("Only the group owner can delete the group");
        }
        groupRepository.delete(group);
    }

    @Override
    public Group joinGroup(final String inviteCode , final Long userId) {
        Group group = getGroupByInviteCodeOrThrow(inviteCode);
        group.addMember(userId, Boolean.FALSE);
        return groupRepository.save(group);
    }

    @Override
    public Group joinGroup(final String inviteCode, final Long userId, final Long guestId) {
        Group group = getGroupByInviteCodeOrThrow(inviteCode);
        group.addMember(userId, Boolean.FALSE);
        group.removeMember(guestId, guestId);
        return groupRepository.save(group);
    }

    @Override
    public void updateGroup(final Group groupToUpdate, final Long requesterId) {
        Group existingGroup = getGroupOrThrow(groupToUpdate.getId());
        if (!existingGroup.isUserAdmin(requesterId)) {
            throw new GroupRequestException("Only admins can update the group");
        }
        existingGroup.setName(groupToUpdate.getName());
        existingGroup.setDescription(groupToUpdate.getDescription());
        existingGroup.setCurrency(groupToUpdate.getCurrency());
        groupRepository.save(existingGroup);
    }

    @Override
    public void kickGroup(final Long groupId, final Long userId, final Long requesterId) {
        Group userGroup = getGroupOrThrow(groupId);
        userGroup.removeMember(userId, requesterId);
        groupRepository.save(userGroup);
    }

    @Override
    public void joinGuestToGroup(final Long groupId, final String name) {
        Group group = getGroupOrThrow(groupId);
        User guestUser = User.builder()
                .name(name)
                .email(StringUtils.generateRandomEmail())
                .build();
        User savedGuest = userService.save(guestUser);

        group.addMember(savedGuest.getId(), Boolean.FALSE);
        groupRepository.save(group);
    }

    @Override
    public void updateGroupInviteCode(final Long groupId, final Long requesterId) {
        Group group = getGroupOrThrow(groupId);
        if (!group.isUserAdmin(requesterId)) {
            throw new GroupRequestException("Only admins can update the invite code");
        }
        generateUniqueInviteCode(group);
        groupRepository.save(group);
    }

    @Override
    public void promoteToAdmin(final Long groupId, final Long userId, final Long requesterId) {
        Group group = getGroupOrThrow(groupId);
        if (!group.isUserAdmin(requesterId)) {
            throw new GroupRequestException("Only admins can promote members to admin");
        }
        group.getMembers().stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .ifPresent(m -> m.setIsAdmin(Boolean.TRUE));
        groupRepository.save(group);
    }

    @Override
    public void leaveGroup(final Long groupId, final Long userId) {
        Group userGroup = getGroupOrThrow(groupId);
        userGroup.removeMember(userId, userId);
        groupRepository.save(userGroup);
    }
    
    private Group getGroupOrThrow(final Long groupId) {
        try {
            Group group = groupRepository.findById(groupId);
            if (group == null) {
                throw new ResourceNotFoundException("Group not found with id: " + groupId);
            }
            return group;
        } catch (GroupNotFoundException e) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }
    }
    
    private Group getGroupByInviteCodeOrThrow(final String inviteCode) {
        try {
            Group group = groupRepository.findByInviteCode(inviteCode);
            if (group == null) {
                throw new ResourceNotFoundException("Group not found with invite code: " + inviteCode);
            }
            return group;
        } catch (GroupNotFoundException e) {
            throw new ResourceNotFoundException("Group not found with invite code: " + inviteCode);
        }
    }
    
    private void generateUniqueInviteCode(final Group group) {
        int maxAttempts = 10;
        int attempts = 0;
        
        while (attempts < maxAttempts) {
            group.setInviteCode();
            
            try {
                Group existing = groupRepository.findByInviteCode(group.getInviteCode());
                if (existing == null || (existing.getId() != null && existing.getId().equals(group.getId()))) {
                    return;
                }
            } catch (GroupNotFoundException e) {
                return;
            }
            
            attempts++;
        }
        throw new GroupRequestException("Failed to generate a unique invite code after " + maxAttempts + " attempts");
    }
}
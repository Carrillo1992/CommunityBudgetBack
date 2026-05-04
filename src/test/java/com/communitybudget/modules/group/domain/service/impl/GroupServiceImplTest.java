package com.communitybudget.modules.group.domain.service.impl;

import com.communitybudget.common.exceptions.exception.GroupNotFoundException;
import com.communitybudget.common.exceptions.exception.GroupRequestException;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.group.domain.model.Group;
import com.communitybudget.modules.group.domain.repository.GroupRepository;
import com.communitybudget.modules.group.domain.valueobjects.Currency;
import com.communitybudget.modules.group.domain.valueobjects.GroupMember;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private GroupServiceImpl groupService;

    private Group group;

    @BeforeEach
    void setUp() {
        group = Group.builder()
                .id(1L)
                .name("Test Group")
                .ownerId(100L)
                .members(new ArrayList<>())
                .build();
    }

    @Test
    void createGroup_ShouldInitializeGroupAndSave() {
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(groupRepository.findByInviteCode(anyString())).thenThrow(new GroupNotFoundException("Not found"));

        Group createdGroup = groupService.createGroup(group);

        assertNotNull(createdGroup.getInviteCode());
        assertNotNull(createdGroup.getCreatedAt());
        assertEquals(1, createdGroup.getMembers().size());
        assertEquals(100L, createdGroup.getMembers().get(0).getUserId());
        assertTrue(createdGroup.getMembers().get(0).getIsAdmin());
        verify(groupRepository).save(any(Group.class));
    }

    @Test
    void createGroup_WhenOwnerIdIsNull_ShouldThrowException() {
        Group groupWithoutOwner = Group.builder().name("Test").build();

        GroupRequestException exception = assertThrows(GroupRequestException.class, () -> groupService.createGroup(groupWithoutOwner));

        assertEquals("Group owner ID cannot be null", exception.getMessage());
        verify(groupRepository, never()).save(any());
    }

    @Test
    void createGroup_WhenInviteCodeCollision_ShouldGenerateNewAndSave() {
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Group existingGroup = Group.builder().id(2L).build();
        when(groupRepository.findByInviteCode(anyString()))
                .thenReturn(existingGroup)
                .thenThrow(new GroupNotFoundException("Not found"));

        Group createdGroup = groupService.createGroup(group);

        assertNotNull(createdGroup.getInviteCode());
        verify(groupRepository, times(2)).findByInviteCode(anyString());
        verify(groupRepository).save(any(Group.class));
    }

    @Test
    void deleteGroup_WhenRequesterIsOwner_ShouldDelete() {
        when(groupRepository.findById(1L)).thenReturn(group);

        groupService.deleteGroup(1L, 100L);

        verify(groupRepository).delete(group);
    }

    @Test
    void deleteGroup_WhenRequesterIsNotOwner_ShouldThrowException() {
        when(groupRepository.findById(1L)).thenReturn(group);

        GroupRequestException exception = assertThrows(GroupRequestException.class, () -> {
            groupService.deleteGroup(1L, 101L);
        });

        assertEquals("Only the group owner can delete the group", exception.getMessage());
        verify(groupRepository, never()).delete(any(Group.class));
    }

    @Test
    void deleteGroup_WhenGroupNotFound_ShouldThrowException() {
        when(groupRepository.findById(1L)).thenThrow(new GroupNotFoundException("Not found"));

        assertThrows(ResourceNotFoundException.class, () -> {
            groupService.deleteGroup(1L, 100L);
        });
    }

    @Test
    void joinGroup_ShouldAddMemberAndSave() {
        when(groupRepository.findByInviteCode("CODE")).thenReturn(group);
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Group updatedGroup = groupService.joinGroup("CODE", 101L);

        assertEquals(1, updatedGroup.getMembers().size());
        assertEquals(101L, updatedGroup.getMembers().get(0).getUserId());
        assertFalse(updatedGroup.getMembers().get(0).getIsAdmin());
        verify(groupRepository).save(group);
    }

    @Test
    void joinGroup_WhenCodeInvalid_ShouldThrowException() {
        when(groupRepository.findByInviteCode("INVALID")).thenThrow(new GroupNotFoundException("Not found"));

        assertThrows(ResourceNotFoundException.class, () -> {
            groupService.joinGroup("INVALID", 101L);
        });
    }

    @Test
    void joinGroup_WithGuestId_ShouldRemoveGuestAndAddUser() {
        group.addMember(200L, false);
        when(groupRepository.findByInviteCode("CODE")).thenReturn(group);
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Group updatedGroup = groupService.joinGroup("CODE", 101L, 200L);

        assertEquals(1, updatedGroup.getMembers().size());
        assertEquals(101L, updatedGroup.getMembers().get(0).getUserId());
        assertFalse(updatedGroup.getMembers().get(0).getIsAdmin());
        verify(groupRepository).save(group);
    }

    @Test
    void updateGroup_WhenRequesterIsAdmin_ShouldUpdateAndSave() {
        group.addMember(100L, true);
        when(groupRepository.findById(1L)).thenReturn(group);
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Group updateInfo = Group.builder()
                .id(1L)
                .name("New Name")
                .description("New Desc")
                .currency(Currency.EUR)
                .build();

        groupService.updateGroup(updateInfo, 100L);

        assertEquals("New Name", group.getName());
        assertEquals("New Desc", group.getDescription());
        assertEquals(Currency.EUR, group.getCurrency());
        verify(groupRepository).save(group);
    }

    @Test
    void updateGroup_WhenRequesterIsNotAdmin_ShouldThrowException() {
        group.addMember(100L, false);
        when(groupRepository.findById(1L)).thenReturn(group);

        Group updateInfo = Group.builder().id(1L).build();

        GroupRequestException exception = assertThrows(GroupRequestException.class, () -> {
            groupService.updateGroup(updateInfo, 100L);
        });

        assertEquals("Only admins can update the group", exception.getMessage());
        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void kickGroup_ShouldRemoveMemberAndSave() {
        group.addMember(100L, true);
        group.addMember(101L, false);
        when(groupRepository.findById(1L)).thenReturn(group);

        groupService.kickGroup(1L, 101L, 100L);

        assertEquals(1, group.getMembers().size());
        assertEquals(100L, group.getMembers().get(0).getUserId());
        verify(groupRepository).save(group);
    }

    @Test
    void joinGuestToGroup_ShouldCreateUserAddMemberAndSave() {
        when(groupRepository.findById(1L)).thenReturn(group);
        User guest = User.builder().id(200L).name("Guest").build();
        when(userService.save(any(User.class))).thenReturn(guest);

        groupService.joinGuestToGroup(1L, "Guest");

        assertEquals(1, group.getMembers().size());
        assertEquals(200L, group.getMembers().get(0).getUserId());
        assertFalse(group.getMembers().get(0).getIsAdmin());
        verify(userService).save(any(User.class));
        verify(groupRepository).save(group);
    }

    @Test
    void updateGroupInviteCode_WhenRequesterIsAdmin_ShouldUpdateCodeAndSave() {
        group.addMember(100L, true);
        String oldCode = group.getInviteCode();
        when(groupRepository.findById(1L)).thenReturn(group);
        when(groupRepository.findByInviteCode(anyString())).thenThrow(new GroupNotFoundException("Not found"));

        groupService.updateGroupInviteCode(1L, 100L);

        assertNotNull(group.getInviteCode());
        assertNotEquals(oldCode, group.getInviteCode());
        verify(groupRepository).save(group);
    }

    @Test
    void updateGroupInviteCode_WhenRequesterIsNotAdmin_ShouldThrowException() {
        group.addMember(100L, false);
        when(groupRepository.findById(1L)).thenReturn(group);

        GroupRequestException exception = assertThrows(GroupRequestException.class, () -> {
            groupService.updateGroupInviteCode(1L, 100L);
        });

        assertEquals("Only admins can update the invite code", exception.getMessage());
        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void promoteToAdmin_WhenRequesterIsAdmin_ShouldPromote() {
        group.addMember(100L, true);
        group.addMember(101L, false);
        when(groupRepository.findById(1L)).thenReturn(group);

        groupService.promoteToAdmin(1L, 101L, 100L);

        GroupMember promoted = group.getMembers().stream()
                .filter(m -> m.getUserId().equals(101L))
                .findFirst().get();

        assertTrue(promoted.getIsAdmin());
        verify(groupRepository).save(group);
    }

    @Test
    void promoteToAdmin_WhenRequesterIsNotAdmin_ShouldThrowException() {
        group.addMember(100L, false);
        when(groupRepository.findById(1L)).thenReturn(group);

        GroupRequestException exception = assertThrows(GroupRequestException.class, () -> {
            groupService.promoteToAdmin(1L, 101L, 100L);
        });

        assertEquals("Only admins can promote members to admin", exception.getMessage());
        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void leaveGroup_ShouldRemoveSelfAndSave() {
        group.addMember(100L, false);
        when(groupRepository.findById(1L)).thenReturn(group);

        groupService.leaveGroup(1L, 100L);

        assertTrue(group.getMembers().isEmpty());
        verify(groupRepository).save(group);
    }
}
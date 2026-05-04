package com.communitybudget.modules.group.domain.model;

import com.communitybudget.common.exceptions.exception.GroupRequestException;
import com.communitybudget.modules.group.domain.valueobjects.GroupMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest {

    private Group group;

    @BeforeEach
    void setUp() {
        group = Group.builder()
                .id(1L)
                .name("Test Group")
                .ownerId(100L)
                .build();
    }

    @Test
    void addMember_WhenNewUser_ShouldAddMemberSuccessfully() {
        group.addMember(100L, true);

        assertEquals(1, group.getMembers().size());
        GroupMember member = group.getMembers().get(0);
        assertEquals(100L, member.getUserId());
        assertTrue(member.getIsAdmin());
        assertNotNull(member.getJoinedAt());
    }

    @Test
    void addMember_WhenUserAlreadyExists_ShouldThrowException() {
        group.addMember(100L, true);

        GroupRequestException exception = assertThrows(GroupRequestException.class, () -> {
            group.addMember(100L, false);
        });

        assertEquals("User is already a member of the group", exception.getMessage());
    }

    @Test
    void removeMember_WhenRequesterIsAdmin_ShouldRemoveMember() {
        group.addMember(100L, true);
        group.addMember(101L, false);

        group.removeMember(101L, 100L);

        assertEquals(1, group.getMembers().size());
        assertEquals(100L, group.getMembers().get(0).getUserId());
    }

    @Test
    void removeMember_WhenRequesterIsSelf_ShouldRemoveMember() {
        group.addMember(101L, false);

        group.removeMember(101L, 101L);

        assertTrue(group.getMembers().isEmpty());
    }

    @Test
    void removeMember_WhenRequesterIsNotAdminAndNotSelf_ShouldThrowException() {
        group.addMember(100L, false);
        group.addMember(101L, false);

        GroupRequestException exception = assertThrows(GroupRequestException.class, () -> {
            group.removeMember(101L, 100L);
        });

        assertEquals("Only admins can remove other members", exception.getMessage());
    }

    @Test
    void isUserAdmin_WhenUserIsAdmin_ShouldReturnTrue() {
        group.addMember(100L, true);

        assertTrue(group.isUserAdmin(100L));
    }

    @Test
    void isUserAdmin_WhenUserIsNotAdmin_ShouldReturnFalse() {
        group.addMember(100L, false);

        assertFalse(group.isUserAdmin(100L));
    }

    @Test
    void isUserAdmin_WhenUserIsNotInGroup_ShouldReturnFalse() {
        assertFalse(group.isUserAdmin(100L));
    }

    @Test
    void setInviteCode_ShouldGenerateStringOfLength8() {
        group.setInviteCode();

        assertNotNull(group.getInviteCode());
        assertEquals(8, group.getInviteCode().length());
    }
}
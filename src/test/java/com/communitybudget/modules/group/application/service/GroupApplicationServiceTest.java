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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupApplicationServiceTest {

    @Mock
    private GroupService groupService;
    
    @Mock
    private GroupMapper groupMapper;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private GroupReadRepository groupReadRepository;

    @InjectMocks
    private GroupApplicationService groupApplicationService;

    private Group group;
    private GroupEntity groupEntity;
    private GroupDto groupDto;

    @BeforeEach
    void setUp() {
        group = Group.builder().id(1L).name("Test Group").build();
        groupEntity = new GroupEntity();
        groupEntity.setId(1L);
        groupDto = new GroupDto();
        groupDto.setId(1L);
    }

    @Test
    void createGroup_ShouldCreateAndReturnDto() {
        CreateGroupRequest request = new CreateGroupRequest();
        when(groupMapper.createGroupDtoToGroup(request, 1L)).thenReturn(group);
        when(groupService.createGroup(group)).thenReturn(group);
        
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        when(groupMapper.createdGroupToGroupDto(group, user)).thenReturn(groupDto);

        GroupDto result = groupApplicationService.createGroup(request, 1L);

        assertEquals(groupDto, result);
        verify(groupService).createGroup(group);
    }

    @Test
    void createGroup_WhenUserNotFound_ShouldThrowException() {
        CreateGroupRequest request = new CreateGroupRequest();
        when(groupMapper.createGroupDtoToGroup(request, 1L)).thenReturn(group);
        when(groupService.createGroup(group)).thenReturn(group);
        
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            groupApplicationService.createGroup(request, 1L);
        });
    }

    @Test
    void getGroupById_ShouldReturnDto() {
        when(groupReadRepository.findById(1L)).thenReturn(groupEntity);
        when(groupMapper.groupEntityToGroupDto(groupEntity)).thenReturn(groupDto);

        GroupDto result = groupApplicationService.getGroupById(1L);

        assertEquals(groupDto, result);
    }

    @Test
    void getMyGroups_ShouldReturnListOfDtos() {
        List<GroupEntity> entities = Collections.singletonList(groupEntity);
        List<GroupDto> dtos = Collections.singletonList(groupDto);
        
        when(groupReadRepository.findAllByUserId(1L)).thenReturn(entities);
        when(groupMapper.groupEntitiesToGroupDtos(entities)).thenReturn(dtos);

        List<GroupDto> result = groupApplicationService.getMyGroups(1L);

        assertEquals(1, result.size());
        assertEquals(groupDto, result.get(0));
    }

    @Test
    void getAllGroups_ShouldReturnListOfDtos() {
        List<GroupEntity> entities = Collections.singletonList(groupEntity);
        List<GroupDto> dtos = Collections.singletonList(groupDto);
        
        when(groupReadRepository.findAll()).thenReturn(entities);
        when(groupMapper.groupEntitiesToGroupDtos(entities)).thenReturn(dtos);

        List<GroupDto> result = groupApplicationService.getAllGroups();

        assertEquals(1, result.size());
        assertEquals(groupDto, result.get(0));
    }

    @Test
    void deleteGroup_ShouldCallService() {
        groupApplicationService.deleteGroup(1L, 100L);
        verify(groupService).deleteGroup(1L, 100L);
    }

    @Test
    void joinGroup_WithInviteCode_ShouldJoinAndReturnDto() {
        when(groupService.joinGroup("CODE", 1L)).thenReturn(group);
        when(groupReadRepository.findById(1L)).thenReturn(groupEntity);
        when(groupMapper.groupEntityToGroupDto(groupEntity)).thenReturn(groupDto);

        GroupDto result = groupApplicationService.joinGroup("CODE", 1L);

        assertEquals(groupDto, result);
    }

    @Test
    void joinGroup_WithGuestId_ShouldJoinAndReturnDto() {
        when(groupService.joinGroup("CODE", 1L, 2L)).thenReturn(group);
        when(groupReadRepository.findById(1L)).thenReturn(groupEntity);
        when(groupMapper.groupEntityToGroupDto(groupEntity)).thenReturn(groupDto);

        GroupDto result = groupApplicationService.joinGroup("CODE", 1L, 2L);

        assertEquals(groupDto, result);
    }

    @Test
    void kickUserFromGroup_ShouldCallService() {
        groupApplicationService.kickUserFromGroup(1L, 2L, 100L);
        verify(groupService).kickGroup(1L, 2L, 100L);
    }

    @Test
    void leaveGroup_ShouldCallService() {
        groupApplicationService.leaveGroup(1L, 2L);
        verify(groupService).leaveGroup(1L, 2L);
    }

    @Test
    void addGuestMember_ShouldAddAndReturnDto() {
        GuestMemberDto request = new GuestMemberDto();
        request.setName("Guest");
        
        when(groupReadRepository.findById(1L)).thenReturn(groupEntity);
        when(groupMapper.groupEntityToGroupDto(groupEntity)).thenReturn(groupDto);

        GroupDto result = groupApplicationService.addGuestMember(1L, request);

        assertEquals(groupDto, result);
        verify(groupService).joinGuestToGroup(1L, "Guest");
    }

    @Test
    void updateGroup_ShouldUpdateAndReturnDto() {
        UpdateGroupDto request = new UpdateGroupDto();
        when(groupMapper.UpdateGroupDtoToGroup(request, 1L)).thenReturn(group);
        when(groupReadRepository.findById(1L)).thenReturn(groupEntity);
        when(groupMapper.groupEntityToGroupDto(groupEntity)).thenReturn(groupDto);

        GroupDto result = groupApplicationService.updateGroup(1L, request, 100L);

        assertEquals(groupDto, result);
        verify(groupService).updateGroup(group, 100L);
    }

    @Test
    void updateGroup_WhenNotFound_ShouldThrowException() {
        UpdateGroupDto request = new UpdateGroupDto();
        when(groupMapper.UpdateGroupDtoToGroup(request, 1L)).thenReturn(group);
        when(groupReadRepository.findById(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            groupApplicationService.updateGroup(1L, request, 100L);
        });
    }

    @Test
    void updateInviteCode_ShouldUpdateAndReturnResponse() {
        groupEntity.setInviteCode("NEW_CODE");
        when(groupReadRepository.findById(1L)).thenReturn(groupEntity);

        InviteCodeResponse result = groupApplicationService.updateInviteCode(1L, 100L);

        assertEquals("NEW_CODE", result.getInviteCode());
        verify(groupService).updateGroupInviteCode(1L, 100L);
    }

    @Test
    void updateInviteCode_WhenNotFound_ShouldThrowException() {
        when(groupReadRepository.findById(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            groupApplicationService.updateInviteCode(1L, 100L);
        });
    }

    @Test
    void promoteToAdmin_ShouldPromoteAndReturnDto() {
        when(groupReadRepository.findById(1L)).thenReturn(groupEntity);
        when(groupMapper.groupEntityToGroupDto(groupEntity)).thenReturn(groupDto);

        GroupDto result = groupApplicationService.promoteToAdmin(1L, 2L, 100L);

        assertEquals(groupDto, result);
        verify(groupService).promoteToAdmin(1L, 2L, 100L);
    }

    @Test
    void promoteToAdmin_WhenNotFound_ShouldThrowException() {
        when(groupReadRepository.findById(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            groupApplicationService.promoteToAdmin(1L, 2L, 100L);
        });
    }
}
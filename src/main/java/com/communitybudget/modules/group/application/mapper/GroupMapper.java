package com.communitybudget.modules.group.application.mapper;

import com.communitybudget.modules.group.application.dto.CreateGroupRequest;
import com.communitybudget.modules.group.application.dto.GroupDto;
import com.communitybudget.modules.group.application.dto.GroupMemberDto;
import com.communitybudget.modules.group.application.dto.UpdateGroupDto;
import com.communitybudget.modules.group.domain.model.Group;
import com.communitybudget.modules.group.domain.valueobjects.GroupMember;
import com.communitybudget.modules.group.infrastructure.persistence.entity.GroupEntity;
import com.communitybudget.modules.group.infrastructure.persistence.entity.GroupMemberEntity;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "currency", expression = "java(group.getCurrency().name())")
    GroupEntity groupToEntity(final Group group);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "group", ignore = true)
    GroupMemberEntity memberToEntity(final GroupMember member);


    Group entityToGroup(final GroupEntity groupEntity);

    @Mapping(target = "userId", source = "user.id")
    GroupMember entityToMember(final GroupMemberEntity memberEntity);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inviteCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ownerId", source = "userId")
    @Mapping(target = "members", ignore = true)
    Group createGroupDtoToGroup(final CreateGroupRequest createGroupRequest,final Long userId);

    @Mapping(target = "id", source = "group.id")
    @Mapping(target = "name", source = "group.name")
    @Mapping(target = "currency",  expression = "java(GroupDto.CurrencyEnum.valueOf(group.getCurrency().name()))")
    @Mapping(target = "members", expression = "java(List.of(userToGroupMemberDto(owner)))")
    GroupDto createdGroupToGroupDto(final Group group , final User owner);


    GroupDto groupEntityToGroupDto(GroupEntity groupEntity);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "avatarUrl", source = "user.avatarUrl")
    GroupMemberDto memberEntityToDto(GroupMemberEntity memberEntity);

    List<GroupDto> groupEntitiesToGroupDtos(final List<GroupEntity> groupEntities);


    @Mapping(target = "id", source = "existingGroupId")
    @Mapping(target = "name", source = "updateGroupDto.name")
    @Mapping(target = "currency", source = "updateGroupDto.currency")
    @Mapping(target = "description", source = "updateGroupDto.description")
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "inviteCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Group UpdateGroupDtoToGroup(final UpdateGroupDto updateGroupDto , final Long existingGroupId);

    default UserEntity mapUserIdToUserEntity(final Long userId) {
        if (userId == null) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        return userEntity;
    }

    default GroupMemberDto userToGroupMemberDto(final User user) {
        if (user == null) {
            return null;
        }
        GroupMemberDto memberDto = new GroupMemberDto();
        memberDto.setId(user.getId());
        memberDto.setName(user.getName());
        memberDto.setAvatarUrl(user.getAvatarUrl());
        return memberDto;
    }

    @AfterMapping
    default void linkMembers(@MappingTarget final GroupEntity groupEntity) {
        if (groupEntity.getMembers() != null) {
            groupEntity.getMembers().forEach(member -> member.setGroup(groupEntity));
        }
    }
}
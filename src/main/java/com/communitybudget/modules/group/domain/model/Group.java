package com.communitybudget.modules.group.domain.model;


import com.communitybudget.common.exceptions.exception.GroupRequestException;
import com.communitybudget.common.utils.StringUtils;
import com.communitybudget.modules.group.domain.valueobjects.Currency;
import com.communitybudget.modules.group.domain.valueobjects.GroupMember;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Group {

    private final Long id;
    private String name;
    private String description;
    private String inviteCode;
    private Currency currency;
    private final Long ownerId;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<GroupMember> members = new ArrayList<>();


    public void addMember(final long userId, final Boolean isAdmin) {
        boolean isAlreadyMember = this.members.stream()
                .anyMatch(m -> m.getUserId().equals(userId));
        if (isAlreadyMember) {
            throw new GroupRequestException("User is already a member of the group");
        }
        GroupMember member = GroupMember.builder()
                .userId(userId)
                .isAdmin(isAdmin)
                .joinedAt(LocalDateTime.now())
                .build();
        this.members.add(member);
    }

    public void removeMember(final Long userId, final Long actionRequesterId ) {

        boolean isRequesterAdmin = isUserAdmin(actionRequesterId);

        if (!isRequesterAdmin && !userId.equals(actionRequesterId)) {
            throw new GroupRequestException("Only admins can remove other members");
        }

        this.members.removeIf(m -> m.getUserId().equals(userId));
    }

    public boolean isUserAdmin(final Long actionRequesterId) {
        return this.members.stream()
                .filter(m -> m.getUserId().equals(actionRequesterId))
                .findFirst()
                .map(GroupMember::getIsAdmin)
                .orElse(false);
    }

    public void setInviteCode() {
        this.inviteCode = StringUtils.generateRandomString(8);
    }


}

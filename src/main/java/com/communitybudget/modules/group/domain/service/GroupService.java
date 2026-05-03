package com.communitybudget.modules.group.domain.service;

import com.communitybudget.modules.group.domain.model.Group;

public interface GroupService {

    void deleteGroup(final Long groupId, final Long requesterId);

    Group joinGroup(final String inviteCode, final Long userId);

    Group joinGroup(final String inviteCode, final Long userId, final Long guestId);

    Group createGroup(final Group group);

    void updateGroup(final Group groupToUpdate, final Long requesterId);

    void leaveGroup(final Long groupId, final Long userId);

    void kickGroup(final Long groupId, final Long userId, final Long requesterId);

    void joinGuestToGroup(final Long groupId, final String name);

    void updateGroupInviteCode(final Long groupId, final Long requesterId);

    void promoteToAdmin(final Long groupId, final Long userId, final Long requesterId);
}

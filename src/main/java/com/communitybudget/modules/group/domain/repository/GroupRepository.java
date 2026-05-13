package com.communitybudget.modules.group.domain.repository;

import com.communitybudget.modules.group.domain.model.Group;

public interface GroupRepository {

    Group save(final Group group);

    Group findById(final Long groupId);

    void delete(final Group group);

    Group findByInviteCode(final String inviteCode);

    Boolean existsById(final Long groupId);

}

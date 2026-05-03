package com.communitybudget.modules.group.domain.repository;

import com.communitybudget.modules.group.domain.model.Group;

import java.util.List;

public interface GroupRepository {


    Group save(final Group group);

    Group findById(final Long groupId);

    void delete(final Group group);

    Group findByInviteCode(final String inviteCode);

}

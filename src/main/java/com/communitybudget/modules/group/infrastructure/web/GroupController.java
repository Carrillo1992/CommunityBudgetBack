package com.communitybudget.modules.group.infrastructure.web;

import com.communitybudget.modules.group.application.dto.*;
import com.communitybudget.modules.group.application.service.GroupApplicationService;
import com.communitybudget.modules.user.application.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/group")
public class GroupController {

    private final GroupApplicationService groupApplicationService;

    public GroupController(final GroupApplicationService groupApplicationService) {
        this.groupApplicationService = groupApplicationService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        List<GroupDto> groups = groupApplicationService.getAllGroups();
        return ResponseEntity.status(200).body(groups);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable final Long groupId) {
        GroupDto group = groupApplicationService.getGroupById(groupId);
        return ResponseEntity.status(200).body(group);
    }

    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@RequestBody final CreateGroupRequest request,
                                                @AuthenticationPrincipal final CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        GroupDto createdGroup = groupApplicationService.createGroup(request, userId);
        return ResponseEntity.status(201).body(createdGroup);
    }

    @GetMapping("/me")
    public ResponseEntity<List<GroupDto>> getMyGroups(@AuthenticationPrincipal final CustomUserDetails userDetails) {
        List<GroupDto> myGroups = groupApplicationService.getMyGroups(userDetails.getId());
        return ResponseEntity.status(200).body(myGroups);
    }

    @GetMapping("/me/{groupId}")
    public ResponseEntity<GroupDto> getMyGroupById(@PathVariable final Long groupId, @AuthenticationPrincipal final CustomUserDetails userDetails) {
        GroupDto group = groupApplicationService.getGroupById(groupId);
        if (group.getMembers().stream().noneMatch(member -> member.getId().equals(userDetails.getId()))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.status(200).body(group);
    }

    @PutMapping("/me/{groupId}/update")
    public ResponseEntity<GroupDto> updateGroup(@PathVariable final Long groupId,
                                                @RequestBody final UpdateGroupDto updateRequest,
                                                @AuthenticationPrincipal final CustomUserDetails userDetails) {
        GroupDto updatedGroup = groupApplicationService.updateGroup(groupId, updateRequest, userDetails.getId());
        return ResponseEntity.status(200).body(updatedGroup);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable final Long groupId,
                                            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        groupApplicationService.deleteGroup(groupId, userDetails.getId());
        return ResponseEntity.status(204).build();
    }

    @PostMapping("/{groupId}/guest")
    public ResponseEntity<GroupDto> addGuestMember(@PathVariable final Long groupId,
                                                   @RequestBody final GuestMemberDto guestMemberRequest) {
        GroupDto guestMember = groupApplicationService.addGuestMember(groupId, guestMemberRequest);
        return ResponseEntity.status(201).body(guestMember);
    }

    @PostMapping("/join/{inviteCode}/{guestId}")
    public ResponseEntity<GroupDto> joinGuestToGroup(@PathVariable final String inviteCode,
                                                     @PathVariable final Long guestId,
                                                     @AuthenticationPrincipal final CustomUserDetails userDetails) {
        GroupDto group = groupApplicationService.joinGroup(inviteCode, userDetails.getId(), guestId);

        return ResponseEntity.status(200).body(group);
    }

    @PostMapping("/join/{inviteCode}")
    public ResponseEntity<GroupDto> joinGroup(@PathVariable final String inviteCode,
                                              @AuthenticationPrincipal final CustomUserDetails userDetails) {
        GroupDto group = groupApplicationService.joinGroup(inviteCode, userDetails.getId());
        return ResponseEntity.status(200).body(group);
    }


    @PostMapping("/me/{groupId}/update/invite-code")
    public ResponseEntity<InviteCodeResponse> updateInviteCode(@PathVariable final Long groupId,
                                                               @AuthenticationPrincipal final CustomUserDetails userDetails) {
        InviteCodeResponse response = groupApplicationService.updateInviteCode(groupId, userDetails.getId());

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/me/{groupId}/promote/{userId}")
    public ResponseEntity<GroupDto> promoteToAdmin(@PathVariable final Long groupId,
                                                   @PathVariable final Long userId,
                                                   @AuthenticationPrincipal final CustomUserDetails userDetails) {
        GroupDto updatedGroup = groupApplicationService.promoteToAdmin(groupId, userId, userDetails.getId());
        return ResponseEntity.status(200).body(updatedGroup);
    }

    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable final Long groupId,
                                           @AuthenticationPrincipal final CustomUserDetails userDetails) {
        groupApplicationService.leaveGroup(groupId, userDetails.getId());
        return ResponseEntity.status(204).build();
    }

    @DeleteMapping("/{groupId}/kick/{userId}")
    public ResponseEntity<Void> kickUserFromGroup(@PathVariable final Long groupId,
                                                  @PathVariable final Long userId,
                                                  @AuthenticationPrincipal final CustomUserDetails userDetails) {
        groupApplicationService.kickUserFromGroup(groupId, userId, userDetails.getId());
        return ResponseEntity.status(204).build();
    }

}
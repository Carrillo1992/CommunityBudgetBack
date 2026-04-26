package com.communitybudget.modules.user.application.service;

import com.communitybudget.common.exceptions.exception.BadRequestException;
import com.communitybudget.common.exceptions.exception.ConflictException;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.user.application.dto.UserCreateDTO;
import com.communitybudget.modules.user.application.dto.UserDTO;
import com.communitybudget.modules.user.application.dto.UserUpdateDTO;
import com.communitybudget.modules.user.domain.model.Role;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.service.UserService;
import com.communitybudget.modules.user.domain.valueobjects.RoleValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserApplicationService userApplicationService;

    private static final Role USER_ROLE = new Role(1L, RoleValue.USER.getValue());

    private User buildUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("Test")
                .email(email)
                .password("encodedPass")
                .provider("local")
                .roles(Set.of(USER_ROLE))
                .build();
    }

    @Test
    void registerUser_savesUserAndReturnsDto() {
        UserCreateDTO dto = new UserCreateDTO("Test", "test@example.com", "Pass@1234");
        when(userService.save(any())).thenReturn(buildUser(1L, "test@example.com"));

        UserDTO result = userApplicationService.registerUser(dto);

        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userService).save(any());
    }

    @Test
    void updateUser_withNewEmail_updatesSuccessfully() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("new@example.com");
        dto.setName("New Name");

        when(userService.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userService.findByEmail("old@example.com")).thenReturn(Optional.of(buildUser(1L, "old@example.com")));

        userApplicationService.updateUser("old@example.com", dto);

        verify(userService).update(any());
    }

    @Test
    void updateUser_withSameEmail_doesNotThrowConflict() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("same@example.com");

        when(userService.findByEmail("same@example.com")).thenReturn(Optional.of(buildUser(1L, "same@example.com")));

        userApplicationService.updateUser("same@example.com", dto);

        verify(userService).update(any());
    }

    @Test
    void updateUser_withNullEmail_preservesExistingEmail() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("New Name");

        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(buildUser(1L, "user@example.com")));

        userApplicationService.updateUser("user@example.com", dto);

        verify(userService).update(any());
    }

    @Test
    void updateUser_whenNewEmailAlreadyTaken_throwsConflictException() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("taken@example.com");

        when(userService.findByEmail("taken@example.com"))
                .thenReturn(Optional.of(buildUser(2L, "taken@example.com")));

        assertThatThrownBy(() -> userApplicationService.updateUser("current@example.com", dto))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void updateUser_whenUserNotFound_throwsResourceNotFoundException() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("Name");

        when(userService.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userApplicationService.updateUser("unknown@example.com", dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateUserById_withNullFields_preservesExistingValues() {
        UserUpdateDTO dto = new UserUpdateDTO();
        User existing = buildUser(1L, "u@u.com");
        when(userService.findById(1L)).thenReturn(Optional.of(existing));

        userApplicationService.updateUserById(1L, dto);

        verify(userService).update(any(User.class));
    }

    @Test
    void updateUserById_whenUserNotFound_throwsResourceNotFoundException() {
        when(userService.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userApplicationService.updateUserById(99L, new UserUpdateDTO()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void changePassword_delegatesToUserService() {
        User user = buildUser(1L, "u@u.com");
        when(userService.findByEmail("u@u.com")).thenReturn(Optional.of(user));

        userApplicationService.changePassword("u@u.com", "current", "New@1234");

        verify(userService).changePassword(user, "current", "New@1234");
    }

    @Test
    void changePassword_whenUserNotFound_throwsResourceNotFoundException() {
        when(userService.findByEmail("none@u.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userApplicationService.changePassword("none@u.com", "a", "b"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getUserByEmail_whenUserExists_returnsDto() {
        when(userService.findByEmail("u@u.com")).thenReturn(Optional.of(buildUser(1L, "u@u.com")));

        UserDTO dto = userApplicationService.getUserByEmail("u@u.com");

        assertThat(dto.getEmail()).isEqualTo("u@u.com");
    }

    @Test
    void getUserByEmail_whenNotFound_throwsResourceNotFoundException() {
        when(userService.findByEmail("x@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userApplicationService.getUserByEmail("x@x.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getUserById_whenUserExists_returnsDto() {
        when(userService.findById(1L)).thenReturn(Optional.of(buildUser(1L, "u@u.com")));

        UserDTO dto = userApplicationService.getUserById(1L);

        assertThat(dto.getEmail()).isEqualTo("u@u.com");
    }

    @Test
    void getUserById_whenNotFound_throwsResourceNotFoundException() {
        when(userService.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userApplicationService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllUsers_returnsListOfDtos() {
        when(userService.findAll()).thenReturn(List.of(buildUser(1L, "a@a.com"), buildUser(2L, "b@b.com")));

        List<UserDTO> result = userApplicationService.getAllUsers();

        assertThat(result).hasSize(2);
    }

    @Test
    void deleteUser_whenUserExists_deletesUser() {
        User user = buildUser(1L, "u@u.com");
        when(userService.findByEmail("u@u.com")).thenReturn(Optional.of(user));

        userApplicationService.deleteUser("u@u.com");

        verify(userService).delete(user);
    }

    @Test
    void deleteUser_whenNotFound_throwsResourceNotFoundException() {
        when(userService.findByEmail("x@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userApplicationService.deleteUser("x@x.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteUserById_whenDeletingAnotherUser_deletesSuccessfully() {
        User other = buildUser(2L, "other@u.com");
        when(userService.findById(2L)).thenReturn(Optional.of(other));

        userApplicationService.deleteUserById(2L, "admin@u.com");

        verify(userService).delete(other);
    }

    @Test
    void deleteUserById_whenDeletingSelf_throwsBadRequestException() {
        User self = buildUser(1L, "me@u.com");
        when(userService.findById(1L)).thenReturn(Optional.of(self));

        assertThatThrownBy(() -> userApplicationService.deleteUserById(1L, "me@u.com"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void deleteUserById_whenNotFound_throwsResourceNotFoundException() {
        when(userService.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userApplicationService.deleteUserById(99L, "admin@u.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void promoteToAdmin_whenUserExists_addsAdminRole() {
        User user = buildUser(1L, "u@u.com");
        when(userService.findById(1L)).thenReturn(Optional.of(user));

        userApplicationService.promoteToAdmin(1L);

        verify(userService).addAdminRole(user);
    }

    @Test
    void promoteToAdmin_whenNotFound_throwsResourceNotFoundException() {
        when(userService.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userApplicationService.promoteToAdmin(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void revokeAdmin_whenRevokingAnotherUser_removesAdminRole() {
        User user = buildUser(2L, "other@u.com");
        when(userService.findById(2L)).thenReturn(Optional.of(user));

        userApplicationService.revokeAdmin(2L, "admin@u.com");

        verify(userService).removeAdminRole(user);
    }

    @Test
    void revokeAdmin_whenRevokingSelf_throwsBadRequestException() {
        User self = buildUser(1L, "me@u.com");
        when(userService.findById(1L)).thenReturn(Optional.of(self));

        assertThatThrownBy(() -> userApplicationService.revokeAdmin(1L, "me@u.com"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void revokeAdmin_whenNotFound_throwsResourceNotFoundException() {
        when(userService.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userApplicationService.revokeAdmin(99L, "admin@u.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void loadUserByUsername_whenUserExists_returnsUserDetails() {
        User user = buildUser(1L, "u@u.com");
        when(userService.findByEmail("u@u.com")).thenReturn(Optional.of(user));

        var details = userApplicationService.loadUserByUsername("u@u.com");

        assertThat(details.getUsername()).isEqualTo("u@u.com");
        assertThat(details.getAuthorities()).isNotEmpty();
    }

    @Test
    void loadUserByUsername_whenNotFound_throwsUsernameNotFoundException() {
        when(userService.findByEmail("x@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userApplicationService.loadUserByUsername("x@x.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}

package com.communitybudget.modules.user.domain.service.impl;

import com.communitybudget.common.exceptions.exception.ConflictException;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.user.domain.exception.InvalidPasswordException;
import com.communitybudget.modules.user.domain.model.Role;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.repository.RoleRepository;
import com.communitybudget.modules.user.domain.repository.UserRepository;
import com.communitybudget.modules.user.domain.service.PasswordEncryptor;
import com.communitybudget.modules.user.domain.valueobjects.RoleValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncryptor passwordEncryptor;

    private UserServiceImpl userService;

    private static final Role USER_ROLE = new Role(1L, RoleValue.USER.getValue());
    private static final Role ADMIN_ROLE = new Role(2L, RoleValue.ADMIN.getValue());

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncryptor, roleRepository);
    }

    private User buildUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("Test User")
                .email(email)
                .password("encodedPassword")
                .provider("local")
                .roles(Set.of(USER_ROLE))
                .build();
    }

    @Test
    void save_whenEmailIsNew_encodesPasswordAndAssignsUserRole() {
        User user = User.builder().name("New").email("new@example.com").password("rawPass").provider("local").build();
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleValue.USER.getValue())).thenReturn(Optional.of(USER_ROLE));
        when(passwordEncryptor.encode("rawPass")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.save(user);

        assertThat(saved.getPassword()).isEqualTo("encoded");
        assertThat(saved.getRoles()).contains(USER_ROLE);
        verify(userRepository).save(any());
    }

    @Test
    void save_whenUserAlreadyHasRoles_preservesExistingRoles() {
        User user = User.builder().email("a@a.com").password("pass").roles(Set.of(ADMIN_ROLE)).build();
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.empty());
        when(passwordEncryptor.encode("pass")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.save(user);

        assertThat(saved.getRoles()).contains(ADMIN_ROLE);
        verify(roleRepository, never()).findByName(any());
    }

    @Test
    void save_whenEmailAlreadyExists_throwsConflictException() {
        User user = User.builder().email("exists@example.com").password("pass").build();
        when(userRepository.findByEmail("exists@example.com"))
                .thenReturn(Optional.of(buildUser(1L, "exists@example.com")));

        assertThatThrownBy(() -> userService.save(user))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("exists@example.com");
    }

    @Test
    void save_whenPasswordIsNull_savesWithoutEncoding() {
        User user = User.builder().email("a@b.com").password(null).provider("google").build();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleValue.USER.getValue())).thenReturn(Optional.of(USER_ROLE));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.save(user);

        assertThat(saved.getPassword()).isNull();
        verify(passwordEncryptor, never()).encode(any());
    }


    @Test
    void changePassword_withValidCredentials_updatesPassword() {
        User user = buildUser(1L, "user@test.com");
        when(passwordEncryptor.matches("current", "encodedPassword")).thenReturn(true);
        when(passwordEncryptor.matches("NewPass@1", "encodedPassword")).thenReturn(false);
        when(passwordEncryptor.encode("NewPass@1")).thenReturn("encodedNew");

        userService.changePassword(user, "current", "NewPass@1");

        verify(userRepository).update(argThat(u -> "encodedNew".equals(u.getPassword())));
    }

    @Test
    void changePassword_whenCurrentPasswordIsWrong_throwsInvalidPasswordException() {
        User user = buildUser(1L, "user@test.com");
        when(passwordEncryptor.matches("wrongPass", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(user, "wrongPass", "NewPass@1"))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("incorrect");
    }

    @Test
    void changePassword_whenNewPasswordSameAsCurrent_throwsInvalidPasswordException() {
        User user = buildUser(1L, "user@test.com");
        when(passwordEncryptor.matches("current", "encodedPassword")).thenReturn(true);

        assertThatThrownBy(() -> userService.changePassword(user, "current", "current"))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("different");
    }


    @Test
    void changePasswordByEmail_whenUserExists_updatesNewEncodedPassword() {
        User user = buildUser(1L, "u@u.com");
        when(userRepository.findByEmail("u@u.com")).thenReturn(Optional.of(user));
        when(passwordEncryptor.encode("newRaw")).thenReturn("newEncoded");

        userService.changePassword("newRaw", "u@u.com");

        verify(userRepository).update(argThat(u -> "newEncoded".equals(u.getPassword())));
    }

    @Test
    void changePasswordByEmail_whenUserNotFound_throwsResourceNotFoundException() {
        when(userRepository.findByEmail("none@u.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword("newPass", "none@u.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addAdminRole_addsAdminRoleToUser() {
        User user = buildUser(1L, "user@test.com");
        when(roleRepository.findByName(RoleValue.ADMIN.getValue())).thenReturn(Optional.of(ADMIN_ROLE));

        userService.addAdminRole(user);

        verify(userRepository).update(argThat(u ->
                u.getRoles().stream().anyMatch(r -> RoleValue.ADMIN.getValue().equals(r.getName()))
        ));
    }

    @Test
    void addAdminRole_whenAdminRoleNotFound_throwsIllegalArgumentException() {
        User user = buildUser(1L, "user@test.com");
        when(roleRepository.findByName(RoleValue.ADMIN.getValue())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addAdminRole(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ADMIN");
    }

    @Test
    void removeAdminRole_whenUserHasBothRoles_removesOnlyAdmin() {
        User user = User.builder()
                .id(1L).name("Admin").email("admin@test.com").password("enc")
                .roles(Set.of(USER_ROLE, ADMIN_ROLE)).provider("local").build();

        userService.removeAdminRole(user);

        verify(userRepository).update(argThat(u ->
                u.getRoles().stream().noneMatch(r -> RoleValue.ADMIN.getValue().equals(r.getName())) &&
                u.getRoles().stream().anyMatch(r -> RoleValue.USER.getValue().equals(r.getName()))
        ));
    }

    @Test
    void removeAdminRole_whenOnlyAdminRole_addsUserRoleBack() {
        User user = User.builder()
                .id(1L).name("Admin").email("admin@test.com").password("enc")
                .roles(Set.of(ADMIN_ROLE)).provider("local").build();
        when(roleRepository.findByName(RoleValue.USER.getValue())).thenReturn(Optional.of(USER_ROLE));

        userService.removeAdminRole(user);

        verify(userRepository).update(argThat(u ->
                u.getRoles().stream().anyMatch(r -> RoleValue.USER.getValue().equals(r.getName()))
        ));
    }

    @Test
    void delete_delegatesToRepository() {
        User user = buildUser(1L, "u@u.com");
        userService.delete(user);
        verify(userRepository).delete(user);
    }

    @Test
    void update_delegatesToRepository() {
        User user = buildUser(1L, "u@u.com");
        userService.update(user);
        verify(userRepository).update(user);
    }

    @Test
    void findAll_delegatesToRepository() {
        when(userRepository.findAll()).thenReturn(List.of(buildUser(1L, "a@a.com")));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findById_delegatesToRepository() {
        User user = buildUser(1L, "u@u.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(userService.findById(1L)).contains(user);
    }

    @Test
    void findByEmail_delegatesToRepository() {
        User user = buildUser(1L, "u@u.com");
        when(userRepository.findByEmail("u@u.com")).thenReturn(Optional.of(user));

        assertThat(userService.findByEmail("u@u.com")).contains(user);
    }
}

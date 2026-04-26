package com.communitybudget.modules.user.infrastructure.web;

import com.communitybudget.common.exceptions.GlobalExceptionHandler;
import com.communitybudget.common.exceptions.exception.BadRequestException;
import com.communitybudget.common.exceptions.exception.ConflictException;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.user.application.dto.ChangePasswordDTO;
import com.communitybudget.modules.user.application.dto.UserDTO;
import com.communitybudget.modules.user.application.dto.UserUpdateDTO;
import com.communitybudget.modules.user.application.service.UserApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserApplicationService userApplicationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userApplicationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private UserDTO buildUserDTO(Long id, String email, String name) {
        UserDTO dto = new UserDTO();
        dto.setId(BigDecimal.valueOf(id));
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }
    private RequestPostProcessor authenticatedAs(String email, String... roles) {
        return request -> {
            List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toList());
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);
            request.setUserPrincipal(auth);
            return request;
        };
    }

    @Test
    void getCurrentUser_whenAuthenticated_returnsUserDto() throws Exception {
        UserDTO userDTO = buildUserDTO(1L, "user@test.com", "Test User");
        when(userApplicationService.getUserByEmail("user@test.com")).thenReturn(userDTO);

        mockMvc.perform(get("/api/v1/user/me")
                .with(authenticatedAs("user@test.com", "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getCurrentUser_whenNotAuthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/user/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_whenUserNotFound_returns404() throws Exception {
        when(userApplicationService.getUserByEmail("user@test.com"))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/user/me")
                .with(authenticatedAs("user@test.com", "USER")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateUser_whenAuthenticated_returns200() throws Exception {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("Updated Name");

        mockMvc.perform(put("/api/v1/user/me")
                .with(authenticatedAs("user@test.com", "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userApplicationService).updateUser(eq("user@test.com"), any(UserUpdateDTO.class));
    }

    @Test
    void updateUser_whenNotAuthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(put("/api/v1/user/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_whenEmailConflict_returns409() throws Exception {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("taken@example.com");

        doThrow(new ConflictException("Email already exists"))
                .when(userApplicationService).updateUser(any(), any());

        mockMvc.perform(put("/api/v1/user/me")
                .with(authenticatedAs("user@test.com", "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void changePassword_whenAuthenticated_returns200() throws Exception {
        ChangePasswordDTO dto = new ChangePasswordDTO("current123", "NewPass@1");

        mockMvc.perform(put("/api/v1/user/me/change-password")
                .with(authenticatedAs("user@test.com", "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userApplicationService).changePassword("user@test.com", "current123", "NewPass@1");
    }

    @Test
    void changePassword_whenNotAuthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(put("/api/v1/user/me/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_whenAuthenticated_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/user/me")
                .with(authenticatedAs("user@test.com", "USER")))
                .andExpect(status().isNoContent());

        verify(userApplicationService).deleteUser("user@test.com");
    }

    @Test
    void deleteUser_whenNotAuthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/user/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllUsers_whenAdmin_returnsListOfUsers() throws Exception {
        List<UserDTO> users = List.of(
                buildUserDTO(1L, "a@a.com", "User A"),
                buildUserDTO(2L, "b@b.com", "User B")
        );
        when(userApplicationService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/user")
                .with(authenticatedAs("admin@test.com", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getUserById_whenAdmin_returnsUser() throws Exception {
        UserDTO userDTO = buildUserDTO(1L, "user@test.com", "Test User");
        when(userApplicationService.getUserById(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/api/v1/user/1")
                .with(authenticatedAs("admin@test.com", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    void getUserById_whenNotFound_returns404() throws Exception {
        when(userApplicationService.getUserById(99L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/v1/user/99")
                .with(authenticatedAs("admin@test.com", "ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateUserById_whenAdmin_returns200() throws Exception {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("Updated");

        mockMvc.perform(put("/api/v1/user/1")
                .with(authenticatedAs("admin@test.com", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userApplicationService).updateUserById(eq(1L), any(UserUpdateDTO.class));
    }

    @Test
    void updateUserById_whenNotFound_returns404() throws Exception {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("Name");

        doThrow(new ResourceNotFoundException("User not found with id: 99"))
                .when(userApplicationService).updateUserById(eq(99L), any());

        mockMvc.perform(put("/api/v1/user/99")
                .with(authenticatedAs("admin@test.com", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserById_whenAdmin_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/user/2")
                .with(authenticatedAs("admin@test.com", "ADMIN")))
                .andExpect(status().isNoContent());

        verify(userApplicationService).deleteUserById(2L, "admin@test.com");
    }

    @Test
    void deleteUserById_whenSelfDeletion_returns400() throws Exception {
        doThrow(new BadRequestException("Cannot delete your own account."))
                .when(userApplicationService).deleteUserById(eq(1L), any());

        mockMvc.perform(delete("/api/v1/user/1")
                .with(authenticatedAs("admin@test.com", "ADMIN")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void promoteToAdmin_whenAdmin_returns200() throws Exception {
        mockMvc.perform(patch("/api/v1/user/2/promote-admin")
                .with(authenticatedAs("admin@test.com", "ADMIN")))
                .andExpect(status().isOk());

        verify(userApplicationService).promoteToAdmin(2L);
    }

    @Test
    void promoteToAdmin_whenUserNotFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("User not found with id: 99"))
                .when(userApplicationService).promoteToAdmin(99L);

        mockMvc.perform(patch("/api/v1/user/99/promote-admin")
                .with(authenticatedAs("admin@test.com", "ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void revokeAdmin_whenAdmin_returns200() throws Exception {
        mockMvc.perform(patch("/api/v1/user/2/revoke-admin")
                .with(authenticatedAs("admin@test.com", "ADMIN")))
                .andExpect(status().isOk());

        verify(userApplicationService).revokeAdmin(2L, "admin@test.com");
    }

    @Test
    void revokeAdmin_whenRevokingSelf_returns400() throws Exception {
        doThrow(new BadRequestException("Cannot revoke your own admin role"))
                .when(userApplicationService).revokeAdmin(eq(1L), any());

        mockMvc.perform(patch("/api/v1/user/1/revoke-admin")
                .with(authenticatedAs("admin@test.com", "ADMIN")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}

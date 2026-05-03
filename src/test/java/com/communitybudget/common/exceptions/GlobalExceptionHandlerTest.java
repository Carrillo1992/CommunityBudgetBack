package com.communitybudget.common.exceptions;

import com.communitybudget.common.exceptions.dto.ErrorResponse;
import com.communitybudget.common.exceptions.exception.*;
import com.communitybudget.modules.user.domain.exception.AuthenticationException;
import com.communitybudget.modules.user.domain.exception.InvalidPasswordException;
import com.communitybudget.modules.user.domain.exception.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleAccessDenied_ShouldReturn403Forbidden() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDenied(ex);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getBody().getStatus());
        assertEquals("Access denied. You do not have the required role to access this resource.", response.getBody().getMessage());
    }

    @Test
    void handleGroupRequestException_WithAdminMessage_ShouldReturn403Forbidden() {
        GroupRequestException ex = new GroupRequestException("Only admins can update the group");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGroupRequestException(ex);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getBody().getStatus());
        assertEquals("Only admins can update the group", response.getBody().getMessage());
    }
    
    @Test
    void handleGroupRequestException_WithOwnerMessage_ShouldReturn403Forbidden() {
        GroupRequestException ex = new GroupRequestException("Only the group owner can delete the group");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGroupRequestException(ex);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getBody().getStatus());
        assertEquals("Only the group owner can delete the group", response.getBody().getMessage());
    }

    @Test
    void handleGroupRequestException_WithOtherMessage_ShouldReturn400BadRequest() {
        GroupRequestException ex = new GroupRequestException("User is already a member");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGroupRequestException(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("User is already a member", response.getBody().getMessage());
    }

    @Test
    void handleResourceNotFound_ShouldReturn404NotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFound(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals("Resource not found", response.getBody().getMessage());
    }

    @Test
    void handleInvalidOperation_ShouldReturn400BadRequest() {
        BadRequestException ex = new BadRequestException("Bad request");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidOperation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Bad request", response.getBody().getMessage());
    }

    @Test
    void handleInvalidPassword_ShouldReturn400BadRequest() {
        InvalidPasswordException ex = new InvalidPasswordException("Invalid password");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidPassword(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Invalid password", response.getBody().getMessage());
    }

    @Test
    void handleUnauthorizedException_ShouldReturn401Unauthorized() {
        UnauthorizedException ex = new UnauthorizedException("Unauthorized");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorizedException(ex);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getMessage());
    }

    @Test
    void handleConflictException_ShouldReturn409Conflict() {
        ConflictException ex = new ConflictException("Conflict occurred");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConflictException(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getStatus());
        assertEquals("Conflict occurred", response.getBody().getMessage());
    }

    @Test
    void handleInvalidToken_ShouldReturn400BadRequest() {
        InvalidTokenException ex = new InvalidTokenException("Invalid token");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidToken(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Invalid token", response.getBody().getMessage());
    }

    @Test
    void handleAuthenticationException_ShouldReturn401Unauthorized() {
        AuthenticationException ex = new AuthenticationException("Authentication failed");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(ex);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getBody().getStatus());
        assertEquals("Authentication failed", response.getBody().getMessage());
    }

    @Test
    void handleGenericException_ShouldReturn500InternalServerError() {
        Exception ex = new Exception("Some unexpected error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals("Internal Server Error: Some unexpected error", response.getBody().getMessage());
    }
}
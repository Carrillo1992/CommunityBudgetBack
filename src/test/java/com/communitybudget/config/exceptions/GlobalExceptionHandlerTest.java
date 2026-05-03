package com.communitybudget.config.exceptions;

import com.communitybudget.common.exceptions.GlobalExceptionHandler;
import com.communitybudget.common.exceptions.exception.BadRequestException;
import com.communitybudget.common.exceptions.exception.ConflictException;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.common.exceptions.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @RestController
    static class TestController {
        @GetMapping("/test/not-found")
        public void throwNotFound() {
            throw new ResourceNotFoundException("Not Found Message");
        }

        @GetMapping("/test/bad-request")
        public void throwBadRequest() {
            throw new BadRequestException("Bad Request Message");
        }

        @GetMapping("/test/unauthorized")
        public void throwUnauthorized() {
            throw new UnauthorizedException("Unauthorized Message");
        }

        @GetMapping("/test/conflict")
        public void throwConflict() {
            throw new ConflictException("Conflict Message");
        }

        @GetMapping("/test/generic")
        public void throwGeneric() {
            throw new RuntimeException("Generic Message");
        }
    }

    @Test
    void whenResourceNotFoundException_thenReturns404() throws Exception {
        mockMvc.perform(get("/test/not-found")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found Message"));
    }

    @Test
    void whenBadRequestException_thenReturns400() throws Exception {
        mockMvc.perform(get("/test/bad-request")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Bad Request Message"));
    }

    @Test
    void whenUnauthorizedException_thenReturns401() throws Exception {
        mockMvc.perform(get("/test/unauthorized")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Unauthorized Message"));
    }

    @Test
    void whenConflictException_thenReturns409() throws Exception {
        mockMvc.perform(get("/test/conflict")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Conflict Message"));
    }

    @Test
    void whenGenericException_thenReturns500() throws Exception {
        mockMvc.perform(get("/test/generic")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Internal Server Error: Generic Message"));
    }
}

package com.pollpulse.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new DummyRestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldDoNotFound_whenResourceDoesNotExist() throws Exception {
        mockMvc.perform(get("/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Election not found"))
                .andExpect(jsonPath("$.path").value("/missing"))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void shouldDoValidationError_whenRequestFieldIsBlank() throws Exception {
        mockMvc.perform(post("/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.name").value("must not be blank"));
    }

    @Test
    void shouldDoConflict_whenResourceConflictOccurs() throws Exception {
        mockMvc.perform(get("/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void shouldDoInternalServerError_whenUnexpectedExceptionOccurs() throws Exception {
        mockMvc.perform(get("/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Unexpected error"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("sensitive"))))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @RestController
    @Validated
    static class DummyRestController {

        @GetMapping("/missing")
        public void missing() {
            throw new ResourceNotFoundException("Election not found");
        }

        @PostMapping("/validation")
        public void validate(@Valid @RequestBody ValidationRequest request) {
        }

        @GetMapping("/conflict")
        public void conflict() {
            throw new ConflictException("Email already exists");
        }

        @GetMapping("/unexpected")
        public void unexpected() throws Exception {
            throw new Exception("sensitive database details");
        }
    }

    record ValidationRequest(@NotBlank(message = "must not be blank") String name) {
    }
}

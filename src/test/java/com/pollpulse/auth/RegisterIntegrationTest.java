package com.pollpulse.auth;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.pollpulse.user.Role;
import com.pollpulse.user.RoleName;
import com.pollpulse.user.RoleRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegisterIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUpRoles() {
        roleRepository.deleteAll();
        for (RoleName name : RoleName.values()) {
            roleRepository.save(new Role(name));
        }
    }

    @Test
    void shouldRegisterAndLogin_whenValidRegistrationIsSubmitted() throws Exception {
        mockMvc.perform(post("/api/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Student User","email":"Student@Example.com","password":"Strong1!x","department":"CSE","batch":"2024","hall":"Hall A"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("student@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(post("/login").with(csrf())
                        .param("email", "student@example.com")
                        .param("password", "Strong1!x"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void shouldReturnFieldErrors_whenPasswordIsWeak() throws Exception {
        mockMvc.perform(post("/api/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Student User","email":"weak@example.com","password":"weak","department":"CSE","batch":"2024","hall":"Hall A"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }
}

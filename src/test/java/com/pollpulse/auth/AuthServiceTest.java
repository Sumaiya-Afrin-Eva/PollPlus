package com.pollpulse.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pollpulse.exception.ConflictException;
import com.pollpulse.user.Role;
import com.pollpulse.user.RoleName;
import com.pollpulse.user.RoleRepository;
import com.pollpulse.user.User;
import com.pollpulse.user.UserRepository;

class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    private AuthService authService;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void shouldThrowConflict_whenEmailAlreadyRegistered() {
        RegisterRequest request = validRequest(" Test@Example.com ");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        ConflictException error = assertThrows(ConflictException.class, () -> authService.register(request));

        assertEquals("Email already registered", error.getMessage());
    }

    @Test
    void shouldStorePasswordAsBCryptHash_whenRegisteringUser() {
        RegisterRequest request = validRequest("student@example.com");
        prepareSuccess(request.email());

        authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        String hash = captor.getValue().getPasswordHash();
        assertTrue(hash.startsWith("$2"));
        assertFalse(hash.equals(request.password()));
        assertTrue(passwordEncoder.matches(request.password(), hash));
    }

    @Test
    void shouldGiveUserRoleVoter_whenRegisteringUser() {
        RegisterRequest request = validRequest("student@example.com");
        prepareSuccess(request.email());

        authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(RoleName.ROLE_VOTER,
                captor.getValue().getRoles().iterator().next().getName());
    }

    @Test
    void shouldSaveEmailInLowerCase_whenRegisteringUser() {
        RegisterRequest request = validRequest("  Student@Example.COM  ");
        prepareSuccess(request.email());

        authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("student@example.com", captor.getValue().getEmail());
    }

    private RegisterRequest validRequest(String email) {
        return new RegisterRequest("Student User", email, "Strong1!x", "CSE", "2024", "Hall A");
    }

    private void prepareSuccess(String email) {
        Role voter = new Role(RoleName.ROLE_VOTER);
        when(roleRepository.findByName(RoleName.ROLE_VOTER)).thenReturn(Optional.of(voter));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.existsByEmail(email.trim().toLowerCase())).thenReturn(false);
    }
}

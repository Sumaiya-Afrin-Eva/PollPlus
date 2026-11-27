package com.pollpulse.auth;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pollpulse.exception.ConflictException;
import com.pollpulse.user.Role;
import com.pollpulse.user.RoleName;
import com.pollpulse.user.RoleRepository;
import com.pollpulse.user.User;
import com.pollpulse.user.UserRepository;
import com.pollpulse.user.UserResponse;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already registered");
        }

        Role voterRole = roleRepository.findByName(RoleName.ROLE_VOTER)
                .orElseThrow(() -> new IllegalStateException("ROLE_VOTER role is missing"));

        User user = new User(
                request.name(), email, passwordEncoder.encode(request.password()),
                request.department(), request.batch(), request.hall());
        user.addRole(voterRole);
        User saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(), saved.getName(), saved.getEmail(), saved.getDepartment(), saved.getBatch(),
                saved.getHall(), saved.isEnabled(),
                List.of(RoleName.ROLE_VOTER.name()));
    }
}

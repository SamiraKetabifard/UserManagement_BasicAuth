package com.example.loginregistration.security;

import com.example.loginregistration.dto.SignUpDto;
import com.example.loginregistration.entity.Role;
import com.example.loginregistration.repository.RoleRepository;
import com.example.loginregistration.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setup() {
        // Ensure required roles exist
        if (!roleRepository.findByName("ROLE_USER").isPresent()) {
            roleRepository.save(new Role("ROLE_USER"));
        }
        if (!roleRepository.findByName("ROLE_ADMIN").isPresent()) {
            roleRepository.save(new Role("ROLE_ADMIN"));
        }
    }
    @Test
    void signUp_ShouldRegisterUser() throws Exception {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setName("samira");
        signUpDto.setUsername("sami");
        signUpDto.setEmail("sam@gmail.com");
        signUpDto.setPassword("12");
        signUpDto.setRole("user");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "name": "samira",
                        "username": "sami",
                        "email": "sam@gmail.com",
                        "password": "12",
                        "role": "user"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
        assertTrue(userRepository.existsByUsername("sami"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_AdminAccess_ShouldReturnUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }
    @Test
    @WithMockUser
    void getAllUsers_NonAdminAccess_ShouldDenyAccess() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }
}

package com.example.loginregistration.controller;

import com.example.loginregistration.dto.SignUpDto;
import com.example.loginregistration.entity.Role;
import com.example.loginregistration.entity.User;
import com.example.loginregistration.repository.RoleRepository;
import com.example.loginregistration.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerUser_Success() {
        // Arrange
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setName("sam");
        signUpDto.setUsername("sami");
        signUpDto.setEmail("s@gmail.com");
        signUpDto.setPassword("password");
        signUpDto.setRole("user");
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new Role("ROLE_USER")));
        when(userRepository.save(any())).thenReturn(new User());
        // Act
        ResponseEntity<?> response = authController.registerUser(signUpDto);
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
        verify(userRepository, times(1)).save(any());
    }
    @Test
    void registerUser_UsernameTaken() {
        // Arrange
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setUsername("existinguser");
        when(userRepository.existsByUsername(any())).thenReturn(true);
        // Act
        ResponseEntity<?> response = authController.registerUser(signUpDto);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username is already taken!", response.getBody());
        verify(userRepository, never()).save(any());
    }
    @Test
    void registerUser_EmailTaken() {
        // Arrange
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setUsername("newuser");
        signUpDto.setEmail("s@gmail.com");
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(true);
        // Act
        ResponseEntity<?> response = authController.registerUser(signUpDto);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is already taken!", response.getBody());
        verify(userRepository, never()).save(any());
    }
    @Test
    void registerUser_RoleNotFound() {
        // Arrange
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setName("reza");
        signUpDto.setUsername("reza12");
        signUpDto.setEmail("reza@gmail.com");
        signUpDto.setPassword("password");
        signUpDto.setRole("invalid");
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_INVALID")).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(RuntimeException.class, () -> authController.registerUser(signUpDto));
        verify(userRepository, never()).save(any());
    }
}

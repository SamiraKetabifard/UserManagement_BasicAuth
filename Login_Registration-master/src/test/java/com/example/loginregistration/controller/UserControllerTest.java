package com.example.loginregistration.controller;

import com.example.loginregistration.entity.User;
import com.example.loginregistration.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserController userController;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_AdminAccess_Success() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);
        // Act
        List<User> result = userController.getAllUsers();
        // Assert
        assertEquals(2, result.size());
    }
    @Test
    void getUserById_Found() {
        // Arrange
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // Act
        ResponseEntity<User> response = userController.getUserById(1L);
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }
    @Test
    void getUserById_NotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        // Act
        ResponseEntity<User> response = userController.getUserById(1L);
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_AdminAccess_Success() {
        // Arrange
        doNothing().when(userRepository).deleteById(1L);
        // Act
        ResponseEntity<HttpStatus> response = userController.deleteUser(1L);
        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
package com.example.loginregistration;

import com.example.loginregistration.entity.Role;
import com.example.loginregistration.entity.User;
import com.example.loginregistration.repository.UserRepository;
import com.example.loginregistration.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_UserFound() {
        // Arrange
        User user = new User();
        user.setUsername("sam");
        user.setPassword("password");
        Role role = new Role("ROLE_USER");
        user.setRoles(Set.of(role));
        when(userRepository.findByUsernameOrEmail("sam", "sam"))
                .thenReturn(Optional.of(user));
        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("sam");
        // Assert
        assertNotNull(userDetails);
        assertEquals("sam", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
    @Test
    void loadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsernameOrEmail("unknown", "unknown"))
                .thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("unknown"));
    }
    @Test
    void loadUserByUsername_EmailFound() {
        // Arrange
        User user = new User();
        user.setUsername("sam");
        user.setEmail("s@gmail.com");
        user.setPassword("password");
        Role role = new Role("ROLE_USER");
        user.setRoles(Set.of(role));
        when(userRepository.findByUsernameOrEmail("s@gmail.com", "s@gmail.com"))
                .thenReturn(Optional.of(user));
        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("s@gmail.com");
        // Assert
        assertNotNull(userDetails);
        assertEquals("sam", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
}
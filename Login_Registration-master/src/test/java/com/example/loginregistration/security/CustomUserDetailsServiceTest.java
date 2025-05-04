package com.example.loginregistration.security;

import com.example.loginregistration.entity.Role;
import com.example.loginregistration.entity.User;
import com.example.loginregistration.repository.UserRepository;
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
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_WithUsername_Success() {
        // Arrange
        User user = new User();
        user.setUsername("samira");
        user.setPassword("12");
        Role role = new Role("ROLE_USER");
        user.setRoles(Set.of(role));
        when(userRepository.findByUsernameOrEmail("samira", "samira"))
                .thenReturn(Optional.of(user));
        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("samira");
        // Assert
        assertEquals("samira", userDetails.getUsername());
        assertEquals("12", userDetails.getPassword());
    }
    @Test
    void loadUserByUsername_WithEmail_Success() {
        // Arrange
        User user = new User();
        user.setUsername("samira");
        user.setEmail("samira@gmail.com");
        user.setPassword("12");
        Role role = new Role("ROLE_USER");
        user.setRoles(Set.of(role));
        when(userRepository.findByUsernameOrEmail("samira@gmail.com", "samira@gmail.com"))
                .thenReturn(Optional.of(user));
        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("samira@gmail.com");
        // Assert
        assertEquals("samira", userDetails.getUsername());
        assertEquals("12", userDetails.getPassword());
    }
    @Test
    void loadUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsernameOrEmail("unknown", "unknown"))
                .thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknown");
        });
    }
}

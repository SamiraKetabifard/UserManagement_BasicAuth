package com.example.loginregistration.repo;

import com.example.loginregistration.entity.User;
import com.example.loginregistration.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Create the users table if it doesn't exist
        entityManager.getEntityManager().createNativeQuery(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                        "email VARCHAR(255), " +
                        "name VARCHAR(255), " +
                        "password VARCHAR(255), " +
                        "username VARCHAR(255))"
        ).executeUpdate();

        // Clear any existing data
        entityManager.getEntityManager().createNativeQuery("DELETE FROM users").executeUpdate();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        entityManager.getEntityManager().createNativeQuery("DELETE FROM users").executeUpdate();
    }

    @Test
    void findByUsernameOrEmail_UsernameFound() {
        // Arrange
        User user = new User();
        user.setUsername("samira");
        user.setEmail("s@gmail.com");
        user.setPassword("password");
        user.setName("samira");
        entityManager.persist(user);
        entityManager.flush();

        // Act - search by username
        Optional<User> found = userRepository.findByUsernameOrEmail("samira", "s@gmail.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("samira", found.get().getUsername());
        assertEquals("s@gmail.com", found.get().getEmail());
    }

    @Test
    void findByUsernameOrEmail_EmailFound() {
        // Arrange
        User user = new User();
        user.setUsername("samira");
        user.setEmail("s@gmail.com");
        user.setPassword("password");
        user.setName("sam");
        entityManager.persist(user);
        entityManager.flush();

        // Act - search by email
        Optional<User> found = userRepository.findByUsernameOrEmail("samira", "s@gmail.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("s@gmail.com", found.get().getEmail());
        assertEquals("samira", found.get().getUsername());
    }

    @Test
    void findByUsernameOrEmail_NotFound() {
        // Act
        Optional<User> found = userRepository.findByUsernameOrEmail("nonexistent",
                "nonexistent@gmail.com");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void existsByUsername_Exists() {
        // Arrange
        User user = new User();
        user.setUsername("samira");
        user.setEmail("s@gmail.com");
        user.setPassword("password");
        user.setName("sam");
        entityManager.persist(user);
        entityManager.flush();

        // Act
        boolean exists = userRepository.existsByUsername("samira");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByUsername_NotExists() {
        // Act
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Assert
        assertFalse(exists);
    }
}
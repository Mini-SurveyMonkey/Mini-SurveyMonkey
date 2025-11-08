package org.example.repository;

import org.example.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This class tests in isolation using an H2 in-memory database
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindById() {
        // Arrange
        User user = new User("alice");

        // Act
        User savedUser = userRepository.save(user);
        Optional<User> found = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("alice");
        assertThat(found.get().getId()).isNotNull();
    }

    @Test
    void testFindByUsername() {
        // Arrange
        User user = new User("lookup_user");
        userRepository.save(user);

        // Act
        Optional<User> found = userRepository.findByUsername("lookup_user");
        Optional<User> notFound = userRepository.findByUsername("does_not_exist");

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.get().getUsername()).isEqualTo("lookup_user");
        assertThat(notFound).isNull();
    }

    @Test
    void testExistsById() {
        // Arrange
        User user = new User("exists_check");
        User saved = userRepository.save(user);

        // Act & Assert
        assertThat(userRepository.existsById(saved.getId())).isTrue();
        assertThat(userRepository.existsById(-999L)).isFalse();
    }

    @Test
    void testDeleteById() {
        // Arrange
        User user = new User("to_be_deleted");
        User saved = userRepository.save(user);

        // Act
        userRepository.deleteById(saved.getId());

        // Assert
        assertThat(userRepository.findById(saved.getId())).isNotPresent();
    }

    @Test
    void testCount() {
        // Arrange
        long initial = userRepository.count();

        userRepository.save(new User("u1"));
        userRepository.save(new User("u2"));

        // Act & Assert
        assertThat(userRepository.count()).isEqualTo(initial + 2);
    }

    @Test
    void testUsernameIsUnique() {
        // Arrange
        userRepository.save(new User("unique_name"));

        // Act & Assert
        assertThatThrownBy(() -> {
            userRepository.save(new User("unique_name"));
        })
        .isInstanceOf(DataIntegrityViolationException.class);
    }
}
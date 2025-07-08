package com.mmcneil.contactmanager;

import com.mmcneil.contactmanager.model.User;
import com.mmcneil.contactmanager.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and retrieve a user")
    void testSaveAndFindUser() {
        User user = new User();
        Set<String> roles = Set.of("user");
        user.setUsername("tester");
        user.setPassword("Hg324jXZ");
        user.setRoles(roles);

        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("tester");
        assertThat(found.get().getPassword()).isEqualTo("Hg324jXZ");
        assertThat(found.get().getRoles()).containsExactlyInAnyOrder("user");
    }

    @Test
    @DisplayName("Should find user by username")
    void testFindByUsername() {
        User user = new User();
        user.setUsername("uniqueuser");
        user.setPassword("pass");
        user.setRoles(Set.of("user"));
        userRepository.save(user);
    
        Optional<User> found = userRepository.findByUsername("uniqueuser");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("uniqueuser");
    }

    @Test
    @DisplayName("Should not allow duplicate usernames")
    void testUniqueUsernameConstraint() {
        User user1 = new User();
        user1.setUsername("dupeuser");
        user1.setPassword("pass1");
        user1.setRoles(Set.of("user"));
        userRepository.save(user1);
    
        User user2 = new User();
        user2.setUsername("dupeuser");
        user2.setPassword("pass2");
        user2.setRoles(Set.of("admin"));
        userRepository.save(user2);
    
        assertThatThrownBy(() -> userRepository.flush())
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}

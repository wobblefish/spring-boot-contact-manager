package com.mmcneil.contactmanager.security;

import com.mmcneil.contactmanager.model.User;
import com.mmcneil.contactmanager.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomUserDetailsServiceTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final CustomUserDetailsService userDetailsService = new CustomUserDetailsService(userRepository);

    @Test
    @DisplayName("Should load user by username and map roles")
    void shouldLoadUserByUsername() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("password");
        user.setRoles(Set.of("user"));

        Mockito.when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("alice");

        assertThat(userDetails.getUsername()).isEqualTo("alice");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.getAuthorities()).extracting("authority")
                .containsExactlyInAnyOrder("ROLE_user");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException if user not found")
    void shouldThrowIfUserNotFound() {
        Mockito.when(userRepository.findByUsername("bob"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("bob"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("bob");
    }
}
package com.mmcneil.contactmanager.service;

import com.mmcneil.contactmanager.exception.UsernameAlreadyExistsException;
import com.mmcneil.contactmanager.model.RegistrationForm;
import com.mmcneil.contactmanager.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_savesNewUser_whenUsernameIsUnique() {
        // Arrange
        RegistrationForm form = new RegistrationForm();
        form.setUsername("alice");
        form.setPassword("password123");
        form.setEmail("alice@example.com");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // Act
        userService.registerUser(form);

        // Assert
        verify(userRepository).save(argThat(user ->
            user.getUsername().equals("alice") &&
            user.getPassword().equals("encodedPassword") &&
            user.getEmail().equals("alice@example.com")
        ));
    }

    @Test
    void registerUser_throwsException_whenUsernameExists() {
        // Arrange
        RegistrationForm form = new RegistrationForm();
        form.setUsername("bob");
        form.setPassword("password123");
        form.setEmail("bob@example.com");

        when(userRepository.existsByUsername("bob")).thenReturn(true);

        // Act & Assert
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.registerUser(form));
        verify(userRepository, never()).save(any());
    }
}

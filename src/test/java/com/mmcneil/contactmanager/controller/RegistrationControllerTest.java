package com.mmcneil.contactmanager.controller;

import com.mmcneil.contactmanager.exception.EmailAlreadyExistsException;
import com.mmcneil.contactmanager.exception.UsernameAlreadyExistsException;
import com.mmcneil.contactmanager.security.SecurityConfig;
import com.mmcneil.contactmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;


@WebMvcTest(RegistrationController.class)
@Import(SecurityConfig.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // Or whatever service handles registration

    @Test
    void getRegisterForm_returnsRegistrationView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register")); // Thymeleaf template name
    }

    @Test
    void postRegister_withValidData_registersAndRedirects() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "alice")
                        .param("password", "password123")
                        .param("email", "alice@example.com")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
    
    @Test
    void postRegister_withDuplicateUsername_showsError() throws Exception {
        doThrow(new UsernameAlreadyExistsException())
                .when(userService).registerUser(any());
    
        mockMvc.perform(post("/register")
            .param("username", "existing")
            .param("password", "password123")
            .param("email", "unique@example.com") // <-- Add this line!
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("userForm", "username"));
    }

    @Test
    void postRegister_withDuplicateEmail_showsError() throws Exception {
        doThrow(new EmailAlreadyExistsException("Email already exists"))
                .when(userService).registerUser(any());

        mockMvc.perform(post("/register")
                .param("username", "newuser")
                .param("password", "password123")
                .param("email", "duplicate@example.com")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"));
    }

    @Test
    void postRegister_withMissingFields_showsValidationErrors() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "")
                .param("password", "")
                .param("email", "")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "username", "password", "email"));
    }

    @Test
    void postRegister_withInvalidEmail_showsValidationError() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "validuser")
                .param("password", "password123")
                .param("email", "not-an-email")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"));
    }
}
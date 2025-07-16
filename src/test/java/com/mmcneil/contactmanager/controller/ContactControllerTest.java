package com.mmcneil.contactmanager.controller;

import com.mmcneil.contactmanager.model.Contact;
import com.mmcneil.contactmanager.repository.ContactRepository;
import com.mmcneil.contactmanager.repository.UserRepository;
import com.mmcneil.contactmanager.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
@WithMockUser(username = "testuser", roles = {"USER"})
class ContactControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactRepository contactRepository;

    @MockBean
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setRoles(Set.of("USER"));
        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }

    @Test
    @DisplayName("GET /contacts should return contact list view with contacts in model")
    void showContactList() throws Exception {
        var contacts = List.of(
            new Contact(1L, "Alice", "alice@example.com", "123-456-7890"),
            new Contact(2L, "Bob", "bob@example.com", "555-555-5555")
        );
        contacts.forEach(c -> c.setUser(testUser));
        Mockito.when(contactRepository.findByUser(testUser)).thenReturn(contacts);
        
        mockMvc.perform(get("/contacts"))
            .andExpect(status().isOk())
            .andExpect(view().name("contact-list"))
            .andExpect(model().attributeExists("contacts"));}

    @Test
    @DisplayName("GET /contacts/new should return create form view with blank contact")
    void showCreateForm() throws Exception {
        mockMvc.perform(get("/contacts/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact-form"))
                .andExpect(model().attributeExists("contact"))
                .andExpect(model().attribute("formAction", "/contacts"))
                .andExpect(model().attribute("formMode", "create"));
    }

    @Test
    @DisplayName("POST /contacts should create contact and redirect on success")
    void createContactSuccess() throws Exception {
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setName("Alice");
        contact.setEmail("alice@example.com");
        contact.setPhone("123-456-7890");
        org.mockito.Mockito.when(contactRepository.save(org.mockito.ArgumentMatchers.any(Contact.class))).thenReturn(contact);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/contacts")
                .param("name", "Alice")
                .param("email", "alice@example.com")
                .param("phone", "123-456-7890")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/contacts"));
    }

    @Test
    @DisplayName("POST /contacts should show validation errors on failure")
    void createContactValidationError() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/contacts")
                .param("name", "") // Name is required
                .param("email", "bad-email") // Invalid email
                .param("phone", "") // Phone is required
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("contact-form"))
                .andExpect(model().attributeHasFieldErrors("contact", "name", "email", "phone"));
    }

    @Test
    @DisplayName("GET /contacts/edit/{id} should return edit form view if contact found")
    void showEditFormFound() throws Exception {
        Contact contact = new Contact(1L, "Alice", "alice@example.com", "123-456-7890");
        contact.setUser(testUser);
        org.mockito.Mockito.when(contactRepository.findById(1L)).thenReturn(java.util.Optional.of(contact));

        mockMvc.perform(get("/contacts/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact-form"))
                .andExpect(model().attributeExists("contact"))
                .andExpect(model().attribute("formAction", "/contacts/edit/1"))
                .andExpect(model().attribute("formMode", "edit"));
    }

    @Test
    @DisplayName("GET /contacts/edit/{id} should handle not found")
    void showEditFormNotFound() throws Exception {
        org.mockito.Mockito.when(contactRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/contacts/edit/99"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/custom-error"))
                .andExpect(model().attributeExists("errorTitle", "errorMessage"));
    }

    @Test
    @DisplayName("POST /contacts/edit/{id} should update contact and redirect on success")
    void updateContactSuccess() throws Exception {
        Contact existing = new Contact(1L, "Alice", "alice@example.com", "123-456-7890");
        existing.setUser(testUser);
        org.mockito.Mockito.when(contactRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        org.mockito.Mockito.when(contactRepository.save(org.mockito.ArgumentMatchers.any(Contact.class))).thenReturn(existing);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/contacts/edit/1")
                .param("name", "Alice Updated")
                .param("email", "alice.updated@example.com")
                .param("phone", "999-888-7777")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/contacts"));
    }

    @Test
    @DisplayName("POST /contacts/edit/{id} should show validation errors on failure")
    void updateContactValidationError() throws Exception {
        Contact existing = new Contact(1L, "Alice", "alice@example.com", "123-456-7890");
        org.mockito.Mockito.when(contactRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/contacts/edit/1")
                .param("name", "") // Name is required
                .param("email", "bad-email") // Invalid email
                .param("phone", "") // Phone is required
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("contact-form"))
                .andExpect(model().attributeHasFieldErrors("contact", "name", "email", "phone"));
    }

    @Test
    @DisplayName("POST /contacts/delete/{id} should delete contact and redirect")
    void deleteContact() throws Exception {
        Contact existing = new Contact(1L, "Alice", "alice@example.com", "123-456-7890");
        existing.setUser(testUser);
        org.mockito.Mockito.when(contactRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        org.mockito.Mockito.when(contactRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/contacts/delete/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/contacts"));
        org.mockito.Mockito.verify(contactRepository).deleteById(1L);
    }
}


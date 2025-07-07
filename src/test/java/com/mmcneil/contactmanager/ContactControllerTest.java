package com.mmcneil.contactmanager;

import com.mmcneil.contactmanager.controller.ContactController;
import com.mmcneil.contactmanager.model.Contact;
import com.mmcneil.contactmanager.repository.ContactRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ContactController.class)
class ContactControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactRepository contactRepository;

    @Test
    @DisplayName("GET /contacts should return contact list view with contacts in model")
    void showContactList() {
        // TODO: Implement test
    }

    @Test
    @DisplayName("GET /contacts/new should return create form view with blank contact")
    void showCreateForm() {
        // TODO: Implement test
    }

    @Test
    @DisplayName("POST /contacts should create contact and redirect on success")
    void createContactSuccess() {
        // TODO: Implement test
    }

    @Test
    @DisplayName("POST /contacts should show validation errors on failure")
    void createContactValidationError() {
        // TODO: Implement test
    }

    @Test
    @DisplayName("GET /contacts/edit/{id} should return edit form view if contact found")
    void showEditFormFound() {
        // TODO: Implement test
    }

    @Test
    @DisplayName("GET /contacts/edit/{id} should handle not found")
    void showEditFormNotFound() {
        // TODO: Implement test
    }

    @Test
    @DisplayName("POST /contacts/edit/{id} should update contact and redirect on success")
    void updateContactSuccess() {
        // TODO: Implement test
    }

    @Test
    @DisplayName("POST /contacts/edit/{id} should show validation errors on failure")
    void updateContactValidationError() {
        // TODO: Implement test
    }

    @Test
    @DisplayName("POST /contacts/delete/{id} should delete contact and redirect")
    void deleteContact() {
        // TODO: Implement test
    }
}


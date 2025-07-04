package com.mmcneil.contactmanager;

import com.mmcneil.contactmanager.controller.ContactRestController;
import com.mmcneil.contactmanager.model.Contact;
import com.mmcneil.contactmanager.repository.ContactRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactRestController.class)
class ContactRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactRepository contactRepository;

    @Test
    @DisplayName("GET /api/contacts should return all contacts as JSON")
    void getAllContacts() throws Exception {
        Contact contact1 = new Contact();
        contact1.setId(1L);
        contact1.setName("Alice Example");
        contact1.setEmail("alice@example.com");
        contact1.setPhone("123-456-7890");

        Contact contact2 = new Contact();
        contact2.setId(2L);
        contact2.setName("Bob Example");
        contact2.setEmail("bob@example.com");
        contact2.setPhone("555-555-5555");

        when(contactRepository.findAll()).thenReturn(Arrays.asList(contact1, contact2));

        mockMvc.perform(get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice Example"))
                .andExpect(jsonPath("$[1].email").value("bob@example.com"));
    }


    @Test
    @DisplayName("GET /api/contacts/{id} should return the contact as JSON")
    void getContactById() throws Exception {
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setName("Alice Example");
        contact.setEmail("alice@example.com");
        contact.setPhone("123-456-7890");
    
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
    
        mockMvc.perform(get("/api/contacts/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Example"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }
    
    @Test
    @DisplayName("GET /api/contacts/{id} should return 404 if not found")
    void getContactByIdNotFound() throws Exception {
        when(contactRepository.findById(99L)).thenReturn(Optional.empty());
    
        mockMvc.perform(get("/api/contacts/99")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
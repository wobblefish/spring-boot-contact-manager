package com.mmcneil.contactmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmcneil.contactmanager.model.Contact;
import com.mmcneil.contactmanager.repository.ContactRepository;

import com.mmcneil.contactmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.mmcneil.contactmanager.model.User;

@SpringBootTest
@AutoConfigureMockMvc
class ContactRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private ContactRepository contactRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // inject the real encoder
    
    @BeforeEach
    void setupUser() {
        userRepository.deleteAll(); // Clean up between tests if needed
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword(passwordEncoder.encode("testpassword"));
        user.setRoles(Set.of("USER")); // Or whatever User entity expects
        userRepository.save(user);
    }
    
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

        when(contactRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(contact1, contact2));

        mockMvc.perform(get("/api/contacts")
        .with(httpBasic("testuser", "testpassword"))
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
        User user = userRepository.findByUsername("testuser").get();
        contact.setUser(user);
    
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
    
        mockMvc.perform(get("/api/contacts/1")
        .with(httpBasic("testuser", "testpassword"))
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
        .with(httpBasic("testuser", "testpassword"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/contacts should create a new contact")
    void createContact() throws Exception {
        Contact contact = new Contact();
        contact.setName("Alice Example");
        contact.setEmail("alice@example.com");
        contact.setPhone("123-456-7890");
    
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> {
            Contact c = invocation.getArgument(0);
            c.setId(1L); // Simulate DB-generated ID
            return c;
        });
    
        mockMvc.perform(post("/api/contacts")
        .with(httpBasic("testuser", "testpassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contact)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice Example"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }
    
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("PUT /api/contacts/{id} should update and return the contact")
    void updateContact() throws Exception {
        Contact existing = new Contact();
        existing.setId(1L);
        existing.setName("Alice Example");
        existing.setEmail("alice@example.com");
        existing.setPhone("123-456-7890");

        Contact updated = new Contact();
        updated.setName("Alice Updated");
        updated.setEmail("alice.updated@example.com");
        updated.setPhone("999-888-7777");

        when(contactRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> {
            Contact c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        mockMvc.perform(put("/api/contacts/1")
                .with(httpBasic("testuser", "testpassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"))
                .andExpect(jsonPath("$.email").value("alice.updated@example.com"));
    }

    @Test
    @DisplayName("PUT /api/contacts/{id} should return 404 if not found")
    void updateContactNotFound() throws Exception {
        Contact updated = new Contact();
        updated.setName("Ghost");
        updated.setEmail("ghost@example.com");
        updated.setPhone("000-000-0000");

        when(contactRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/contacts/99")
                .with(httpBasic("testuser", "testpassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/contacts/{id} should delete the contact")
    void deleteContact() throws Exception {
        mockMvc.perform(delete("/api/contacts/1")
                .with(httpBasic("testuser", "testpassword")))
                .andExpect(status().isOk());
        verify(contactRepository).deleteById(1L);
    }

    @Test
    @DisplayName("GET /api/contacts should only return contacts belonging to the authenticated user")
    void getAllContacts_onlyReturnsOwnContacts() throws Exception {
        // Setup: Create two users and save to the real DB
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword(passwordEncoder.encode("password1"));
        user1.setRoles(Set.of("USER"));
        userRepository.save(user1);
    
        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword(passwordEncoder.encode("password2"));
        user2.setRoles(Set.of("USER"));
        userRepository.save(user2);
    
        // Fetch the managed instances (these are the instances the controller will use)
        User dbUser1 = userRepository.findByUsername("user1").get();
        User dbUser2 = userRepository.findByUsername("user2").get();
    
        // Setup: Create a contact for each user
        Contact contact1 = new Contact();
        contact1.setName("Contact1");
        contact1.setEmail("c1@example.com");
        contact1.setPhone("111-111-1111");
        contact1.setUser(dbUser1);
    
        Contact contact2 = new Contact();
        contact2.setName("Contact2");
        contact2.setEmail("c2@example.com");
        contact2.setPhone("222-222-2222");
        contact2.setUser(dbUser2);

        // Only mock the contact repository, using argument matchers for robust matching
        when(contactRepository.findByUser(argThat(u -> u != null && u.getUsername().equals("user1")))).thenReturn(Arrays.asList(contact1));
        when(contactRepository.findByUser(argThat(u -> u != null && u.getUsername().equals("user2")))).thenReturn(Arrays.asList(contact2));

        // Act: user1 requests their contacts
        mockMvc.perform(get("/api/contacts")
                        .with(httpBasic("user1", "password1"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Contact1"))
                .andExpect(jsonPath("$[0].email").value("c1@example.com"))
                .andExpect(jsonPath("$", hasSize(1))); // Only one contact returned

        // Act: user2 requests their contacts
        mockMvc.perform(get("/api/contacts")
                        .with(httpBasic("user2", "password2"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Contact2"))
                .andExpect(jsonPath("$[0].email").value("c2@example.com"))
                .andExpect(jsonPath("$", hasSize(1))); // Only one contact returned
    }

    @Test
    @DisplayName("GET /api/contacts/{id} returns 404 if contact does not belong to authenticated user")
    void getContactById_forOtherUser_returns404() throws Exception {
        // Setup: Create two users and save to the real DB
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword(passwordEncoder.encode("password1"));
        user1.setRoles(Set.of("USER"));
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword(passwordEncoder.encode("password2"));
        user2.setRoles(Set.of("USER"));
        userRepository.save(user2);

        // Fetch managed instance for user2
        User dbUser2 = userRepository.findByUsername("user2").get();

        // Setup: Create a contact for user2
        Contact contact2 = new Contact();
        contact2.setId(42L);
        contact2.setName("Contact2");
        contact2.setEmail("c2@example.com");
        contact2.setPhone("222-222-2222");
        contact2.setUser(dbUser2);

        // Only mock the contact repository for findById
        when(contactRepository.findById(42L)).thenReturn(Optional.of(contact2));

        // Act: user1 tries to access user2's contact by ID
        mockMvc.perform(get("/api/contacts/42")
                        .with(httpBasic("user1", "password1"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
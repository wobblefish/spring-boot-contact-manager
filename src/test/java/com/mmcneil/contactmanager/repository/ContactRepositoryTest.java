package com.mmcneil.contactmanager.repository;

import com.mmcneil.contactmanager.model.Contact;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ContactRepositoryTest {
    @Autowired
    private ContactRepository contactRepository;

    @Test
    @DisplayName("Should save and retrieve a contact")
    void testSaveAndFindContact() {
        Contact contact = new Contact();
        contact.setName("Alice Example");
        contact.setEmail("alice@example.com");
        contact.setPhone("123-456-7890");

        Contact saved = contactRepository.save(contact);
        Optional<Contact> found = contactRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice Example");
        assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
        assertThat(found.get().getPhone()).isEqualTo("123-456-7890");
    }

    @Test
    @DisplayName("Should delete a contact by ID")
    void testDeleteContact() {
        Contact contact = new Contact();
        contact.setName("Bob Example");
        contact.setEmail("bob@example.com");
        contact.setPhone("555-555-5555");

        Contact saved = contactRepository.save(contact);
        Long id = saved.getId();

        contactRepository.deleteById(id);
        Optional<Contact> found = contactRepository.findById(id);

        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Should update an existing contact")
    void testUpdateContact() {
        Contact contact = new Contact();
        contact.setName("Carol Example");
        contact.setEmail("carol@example.com");
        contact.setPhone("111-222-3333");
    
        Contact saved = contactRepository.save(contact);
        Long id = saved.getId();
    
        // Update
        saved.setEmail("carol.updated@example.com");
        saved.setPhone("999-888-7777");
        contactRepository.save(saved);
    
        Optional<Contact> found = contactRepository.findById(id);
    
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("carol.updated@example.com");
        assertThat(found.get().getPhone()).isEqualTo("999-888-7777");
    }

    @Test
    @DisplayName("Should save and retrieve a new contact")
    void createContact() {
        // Arrange: create a new Contact
        Contact contact = new Contact();
        contact.setName("Carol Example");
        contact.setEmail("carol@example.com");
        contact.setPhone("111-222-3333");

        // Act: save the contact
        Contact saved = contactRepository.save(contact);

        // Assert: retrieve by ID and check fields
        Optional<Contact> found = contactRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Carol Example");
        assertThat(found.get().getEmail()).isEqualTo("carol@example.com");
        assertThat(found.get().getPhone()).isEqualTo("111-222-3333");
    }

    @Test
    @DisplayName("Should find all contacts")
    void findAllContacts() {
        Contact c1 = new Contact();
        c1.setName("Alice");
        c1.setEmail("alice@example.com");
        c1.setPhone("111-111-1111");
    
        Contact c2 = new Contact();
        c2.setName("Bob");
        c2.setEmail("bob@example.com");
        c2.setPhone("222-222-2222");
    
        contactRepository.save(c1);
        contactRepository.save(c2);
    
        List<Contact> all = contactRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
        assertThat(all).extracting(Contact::getName).contains("Alice", "Bob");
    }

    @Test
    @DisplayName("Should return empty Optional when contact not found")
    void findByIdNotFound() {
        Optional<Contact> found = contactRepository.findById(1L);
        assertThat(found).isNotPresent();
    }
}

package com.mmcneil.contactmanager;

import com.mmcneil.contactmanager.model.Contact;
import com.mmcneil.contactmanager.repository.ContactRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
}

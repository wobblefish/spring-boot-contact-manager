package com.mmcneil.contactmanager.controller;

import com.mmcneil.contactmanager.model.Contact;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/contacts")
public class ContactRestController {

    // Simulated in-memory "database"
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<Contact> contacts = new ArrayList<>();

    @GetMapping
    public List<Contact> getContacts() {
        return contacts;
    }

    @GetMapping("/{id}")
    public Contact getContactsById(@PathVariable long id) {
        for (Contact contact : contacts) {
            if (contact.getId() == id) {
                return contact;
            }
        }
        return null;
    }

    @PostMapping
    public Contact addContact(@RequestBody Contact contact) {
        System.out.println("Received contact: " + contact);
        contacts.add(contact);
        return contact;
    }

}

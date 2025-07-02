package com.mmcneil.contactmanager.controller;

import com.mmcneil.contactmanager.model.Contact;
import com.mmcneil.contactmanager.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contacts")
public class ContactController {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @GetMapping
    public String showContactList(Model model) {
        model.addAttribute("contacts", contactRepository.findAll());
        return "contact-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("contact", new Contact());
        return "contact-form";
    }

    @PostMapping
    public String addContact(@ModelAttribute Contact contact) {
        contactRepository.save(contact);
        return "redirect:/contacts";
    }

    // Misconception - assumed this should be a DELETE but HTML forms only know GET/POST so a DeleteMapping would be ignored in this case
    @PostMapping("/delete/{id}")
    public String deleteContact(@PathVariable Long id) {
        contactRepository.deleteById(id);
        return "redirect:/contacts";
    }
}

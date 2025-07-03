package com.mmcneil.contactmanager.controller;

import com.mmcneil.contactmanager.model.Contact;
import com.mmcneil.contactmanager.repository.ContactRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        model.addAttribute("formAction", "/contacts");
        model.addAttribute("formMode", "create");
        return "contact-form";
    }

    @PostMapping
    public String addContact(@Valid @ModelAttribute Contact contact,
        BindingResult result,
        Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("formAction", "/contacts");
            model.addAttribute("formMode", "create");
            return "contact-form";
        }
        contactRepository.save(contact);
        return "redirect:/contacts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Contact contact = contactRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid contact Id:" + id));
        model.addAttribute("contact", contact);
        model.addAttribute("formAction", "/contacts/edit/" + id);
        model.addAttribute("formMode", "edit");
        return "contact-form"; // reuse the same form for create/edit
    }

    @PostMapping("/edit/{id}")
    public String updateContact(
        @PathVariable Long id,
        @Valid @ModelAttribute Contact contact,
        BindingResult result,
        Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("formAction", "/contacts/edit/" + id);
            model.addAttribute("formMode", "edit");
            return "contact-form";
        }
        // Fetch existing contact
        Contact existingContact = contactRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid contact Id:" + id));
        // Update fields
        existingContact.setName(contact.getName());
        existingContact.setEmail(contact.getEmail());
        existingContact.setPhone(contact.getPhone());
        contactRepository.save(existingContact);
        return "redirect:/contacts";
    }

    // Misconception - assumed this should be a DELETE but HTML forms only know GET/POST so a DeleteMapping would be ignored in this case
    @PostMapping("/delete/{id}")
    public String deleteContact(@PathVariable Long id) {
        contactRepository.deleteById(id);
        return "redirect:/contacts";
    }
}

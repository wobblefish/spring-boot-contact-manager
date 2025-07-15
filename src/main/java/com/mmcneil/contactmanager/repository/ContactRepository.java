package com.mmcneil.contactmanager.repository;

import com.mmcneil.contactmanager.model.Contact;
import com.mmcneil.contactmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUser(User user);
}

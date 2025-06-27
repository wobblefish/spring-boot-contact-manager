package com.mmcneil.contactmanager.repository;

import com.mmcneil.contactmanager.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}

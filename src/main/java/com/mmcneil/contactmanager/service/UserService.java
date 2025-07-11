package com.mmcneil.contactmanager.service;

import com.mmcneil.contactmanager.exception.EmailAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mmcneil.contactmanager.exception.UsernameAlreadyExistsException;
import com.mmcneil.contactmanager.model.RegistrationForm;
import com.mmcneil.contactmanager.model.User;
import com.mmcneil.contactmanager.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public void registerUser(RegistrationForm form) {
        if (userRepository.existsByUsername(form.getUsername().toLowerCase())) {
            throw new UsernameAlreadyExistsException();
        }
        if (userRepository.existsByEmail(form.getEmail().toLowerCase())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        // create a User entity from the RegistrationForm
        User user = new User();
        user.setUsername(form.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setEmail(form.getEmail().toLowerCase());
        userRepository.save(user);
    }
}

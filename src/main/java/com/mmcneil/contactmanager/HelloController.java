package com.mmcneil.contactmanager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/contactmanager")
    public String hello() {
        return "Hello, and welcome to the future home of the contact manager app!";
    }
}


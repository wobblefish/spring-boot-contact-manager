### Spring Boot Contact Manager - Learning Journey & Technical Milestones

This project served as a progressive learning platform for modern Java development with Spring Boot. Below is a breakdown of the major milestones achieved and the concepts I explored and applied.


## Project Initialization

- **Spring Boot Scaffolded** using Spring Initializr with dependencies:
    - Spring Web
    - Spring Data JPA
    - Thymeleaf
    - H2 Database


## Basic Java and Spring Boot Routing

- Created a basic `Contact` class with just in-memory fields (no database or annotations initially)
- Manually managed IDs with a counter to simulate persistence
- Used `@RestController` with basic `@GetMapping` and `@PostMapping` endpoints to return dummy contacts and simulate basic HTTP API flow


## Introduced JPA and H2 In-Memory Database

- Added Spring Data JPA to the project
- Annotated the `Contact` class with:


@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;
}

- Replaced in-memory list and ID counter with real database persistence
- Enabled H2 in-memory database and console view

## Repository Layer

- Created ContactRepository by extending JpaRepository<Contact, Long>
- This gave full access to CRUD methods without manually implementing logic

## REST API Implementation

Started with the backend REST API to get a feel for HTTP methods, path mapping, and auto-serialization.

- Implemented @GetMapping, @PostMapping, and @DeleteMapping endpoints
- Used @RequestBody, @PathVariable to bind request data
- Validated that JSON requests/responses were working

## Introduction to Thymeleaf (Server-side Rendering)

After verifying the API worked, I shifted to front-end rendering with Thymeleaf.

- Created views for contact list and creation form
- Used @Controller and Model to pass data to views
- Implemented routes for showing contact list, creation form, and submitting new contacts

## Routing & Resource Handling

- Learned about Thymeleaf template locations and view name conventions
- Understood the importance of proper HTML nesting and loop handling

## Migration to PostgreSQL and Environment Variables

- Replaced H2 with PostgreSQL for a more robust database solution
- Configured environment variables for database connection properties
- Updated application.properties to use env vars for database configuration

## Enhancing UI with Bootstrap 5 and Icons

- Integrated Bootstrap 5 for a responsive and visually appealing UI
- Added icons to enhance the user experience

## Custom Error Pages and Global Exception Handler

- Created custom error pages for a better user experience
- Implemented a global exception handler using @ControllerAdvice and @ExceptionHandler

## Debugging & Design Insights

- Utilized Spring's Whitelabel Error Page for error diagnosis
- Identified common issues like missing model attributes and malformed HTML
- Learned about HTML form method constraints and how to handle them

## Planning Forward
- Additional documentation
- Add testing using JUnit and Spring Boot Test
- Deployment options: Heroku, Fly.io, Render
- Add Users, user authentication and authorization (Spring Security)

- Future:
    - Plan to refactor REST controller methods to use ResponseEntity for better status code handling
    - Since the REST API is intended for consumption by external clients, such as mobile apps, and not for internal HTML forms
        - Later integration with mobile apps

## Annotations Learned

- @SpringBootApplication
- @Entity, @Id, @GeneratedValue
- @RestController, @Controller
- @RequestMapping, @GetMapping, @PostMapping, @DeleteMapping
- @Autowired, @PathVariable, @RequestBody, @ModelAttribute
- @Valid, @NotBlank, @Email
- @ControllerAdvice, @ExceptionHandler
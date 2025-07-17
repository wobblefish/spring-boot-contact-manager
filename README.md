[Live Demo (Railway)](https://spring-boot-contact-manager-production.up.railway.app/)

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

# Testing

## 1. JPA Repository Tests (ContactRepositoryTest)
Coverage:
- Save and retrieve by ID
- Update existing contact
- Delete contact by ID
- Find all contacts
- Retrieve non-existent contact (returns empty)

## 2. REST Controller Tests (ContactRestControllerTest)
Coverage:
- GET /api/contacts — Returns all contacts (200 OK)
- GET /api/contacts/{id} — Returns contact if found (200 OK), 404 if not found
- POST /api/contacts — Creates contact (201 Created, Location header, JSON body)
- PUT /api/contacts/{id} — Updates contact if found (200 OK), 404 if not found
- DELETE /api/contacts/{id} — Deletes contact (200 OK), verifies repository interaction

## 3. Web (MVC/Thymeleaf) Controller Tests (ContactControllerTest)
Coverage:
- GET /contacts — Shows contact list view, model contains all contacts
- GET /contacts/new — Shows create form with blank contact, correct model attributes
- POST /contacts — Creates contact and redirects on success; shows validation errors on failure
- GET /contacts/edit/{id} — Shows edit form if found; renders error page if not found
- POST /contacts/edit/{id} — Updates and redirects on success; shows validation errors on failure
- POST /contacts/delete/{id} — Deletes contact and redirects, verifies repository interaction

## Spring Security Integration Milestones (TDD)

**Established user entity, repository, and unique constraints:**
- Added `User` entity with JPA annotations: `@Entity`, `@Table(name = "users")`, `@Id`, `@GeneratedValue`, `@Column(unique = true)`
- Used `Set<String>` for unique roles with `@ElementCollection` and `@CollectionTable`
- Created `UserRepository` extending `JpaRepository<User, Long>` with custom `findByUsername` method
- Wrote and passed repository tests for save, find, and unique constraint (using `DataIntegrityViolationException`)
- **Gotcha:** Avoided reserved SQL keywords by naming table `users` instead of `user`

**Integrated Spring Security user loading and role mapping:**
- Added dependency on Spring Security.
- Implemented `CustomUserDetailsService` to load users by username, map roles to `SimpleGrantedAuthority`, and return a Spring Security `User` object.
- Used Java Streams for efficient authority mapping.
- Wrote and passed unit tests for user loading, not-found handling, and correct role mapping.
- **Gotcha:** Used interface-driven design for testability and Spring Security integration; mapped roles as `ROLE_` prefixed authorities.

**Configured security filter chains and authentication flows:**
- Implemented `SecurityConfig` using `SecurityFilterChain` beans.
- Two filter chains:
    - **API chain (`/api/**`)**: Stateless, HTTP Basic, always returns 401 for unauthenticated or invalid credentials.
    - **Web chain (all other endpoints)**: Form login, redirects unauthenticated users to `/login`.
- Permitted `/error` in both chains to prevent error dispatches from falling through to the web chain (fixes 401→302 bug for REST APIs).
- Registered a `PasswordEncoder` (BCrypt) for secure one-way password hashing, ensuring passwords are never stored in plain text and protecting against common attack vectors. 
- Added minimal controllers/templates for `/login` and `/`.
- Verified endpoint protection with integration tests (`SecurityConfigTest`) using MockMvc and `@WithMockUser`.
- **Key learnings:**
    - `SecurityFilterChain` is the standard for multi-chain security config in Spring Boot 3+.
    - `AuthenticationEntryPoint` and `AccessDeniedHandler` control responses for unauthenticated/unauthorized requests.
    - `UsernameNotFoundException` is the key exception for user-not-found scenarios in authentication flows.
    - Permitting `/error` prevents security chain fallthrough and unwanted redirects on error dispatches.
    - Test context (`MockMvc`) vs. real HTTP context can affect observed behavior.
    - Test annotations (`@DataJpaTest`, `@WebMvcTest`, `@SpringBootTest`) load different app contexts and DB configs.
- **Gotcha:** Without permitting `/error`, error dispatches from API endpoints can fall through to the web chain, causing 302 redirects to `/login` instead of 401s.

**Implemented registration and login flows with validation and modern UI:**

- Implemented user registration using a dedicated `RegistrationForm` DTO with validation for username, password, and email fields and Thymeleaf template for registration.
- Added comprehensive validation and error handling for duplicate usernames and emails, including user-friendly error messages.
- Modernized registration, login, and welcome pages using Bootstrap 5 and Bootstrap Icons for improved user experience.
- Integrated Thymeleaf Spring Security extras for conditional UI rendering based on authentication state.
- Developed and passed controller tests for registration success, validation errors, and unique constraint violations.
- Updated repository tests to verify unique constraints for username and email using `DataIntegrityViolationException` at the correct transaction boundary.
- **Gotchas:**
    - Validation annotations must be present on the DTO (not just the entity) for form validation to trigger in the web layer.
    - Service-level exceptions for duplicate username/email are only thrown if validation passes; validation errors short-circuit before hitting the service.
    - Spring’s exception translation wraps database unique constraint violations as `DataIntegrityViolationException`; always test at the correct transaction boundary (`save()`).
    - Form error display requires proper use of `@Valid` and `BindingResult` in controller method signatures to bind and show errors in Thymeleaf.
    - UI error messages must be mapped to the correct field names in the form to ensure user-friendly feedback.

### User-Scoped Data Access & REST API Security
- Refactored all contact-related endpoints (web and REST) to enforce user-scoped data: every contact is always associated with a specific user, and users can only access, modify, or delete their own contacts.
- Updated both MVC and REST controllers to retrieve the current user from the security context and query contacts using `findByUser(...)` or equivalent ownership checks.
- Hardened controller logic to return 404 Not Found for attempts to access another user's contact, preventing both data leaks and accidental NPEs.
- Added/updated tests to verify user isolation for all endpoints, including REST API, using both MockMvc with HTTP Basic and real database users.
- Ensured all test and seed data assigns a valid user to each contact, and added defensive checks to avoid null user references.
- Verified REST endpoints return 401 Unauthorized for unauthenticated requests and 404 for unauthorized access, matching best practices for RESTful security.

**Gotchas:**
- All contacts in the database must have a non-null user field; missing associations will cause 500 errors.
- When testing REST endpoints, use HTTP Basic Auth (not `@WithMockUser`), and ensure the test user exists in the database with a valid password.
- Ownership checks must be enforced at the controller/service layer for every data access, not just queries.
---

## Planning Forward
- Deployment options: Heroku, Fly.io, Render

- Future:
    - Plan to refactor REST controller methods to use ResponseEntity for better status code handling
    - Since the REST API is intended for consumption by external clients, such as mobile apps, and not for internal HTML forms
        - Later integration with mobile apps

## Annotations Learned

**Annotations & Test Utilities**
- `@SpringBootApplication` — Main Spring Boot application entry point.
- `@Override` — Indicates a method overrides a superclass method.
- `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column(unique = true)` — JPA entity and mapping annotations for persistence.
- `@RestController`, `@Controller` — Marks a class as a REST or MVC controller.
- `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@DeleteMapping` — Defines HTTP endpoint mappings.
- `@Autowired`, `@PathVariable`, `@RequestBody`, `@ModelAttribute` — Dependency injection and request parameter binding.
- `@Valid`, `@NotBlank`, `@Email` — Validation annotations for request data.
- `@ControllerAdvice`, `@ExceptionHandler` — Global exception and error handling.
- `@ElementCollection`, `@CollectionTable` — JPA annotations for mapping collections.
- `@Bean` — Registers configuration objects (e.g., `SecurityFilterChain`, `PasswordEncoder`) with the Spring context.
- `@MockBean` — Injects mocks for dependencies in Spring Boot tests (e.g., repositories).
- `@WithMockUser` — Simulates different users/roles in security tests, abstracting away real user setup.
- `@SpringBootTest` — Loads the full application context for integration tests, including security and DB config.
- `@ManyToOne` — Associates each contact with a user (ownership) in JPA.
- `@RequestHeader("Authorization")` — Used in REST controller tests to pass HTTP Basic or Bearer tokens.
- `httpBasic(...)` (MockMvc) — Utility for simulating HTTP Basic authentication in REST tests.
- Defensive null checks — Always verify entity relationships before dereferencing in API logic.


## Interfaces & Exceptions Introduced

**Interfaces & Exceptions Introduced**
- `JpaRepository` — Spring Data repository base interface for CRUD operations.
- `UserDetailsService` — Loads user for Spring Security authentication.
- `UserDetails` — Spring Security user contract.
- `SimpleGrantedAuthority` — Wrapper for user roles/authorities in security.
- `SecurityFilterChain` — Defines security rules for endpoint groups (modern replacement for `WebSecurityConfigurerAdapter`).
- `PasswordEncoder` — Encodes and verifies passwords (e.g., `BCryptPasswordEncoder`).
- `AuthenticationEntryPoint` — Handles unauthenticated requests (e.g., returns 401 for APIs).
- `AccessDeniedHandler` — Handles unauthorized requests (e.g., returns 401 for APIs).
- `DataIntegrityViolationException` — JPA unique constraint violation error.
- `UsernameNotFoundException` — Thrown when a user is not found.
- `ContactNotFoundException` — Custom exception for missing contacts.
- `NoHandlerFoundException` — Spring MVC 404 handler for unmapped URLs.
- `ResponseStatusException` — Used to return 404 or 401 from REST controllers on access/ownership violations.
- User-Scoped Query Methods — Custom repository methods like `findByUser(User user)` to enforce data isolation.
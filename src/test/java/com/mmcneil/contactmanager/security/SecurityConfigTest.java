package com.mmcneil.contactmanager.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /contacts should require authentication")
    void contactsRequiresAuthentication() throws Exception {
        // Perform a GET to /contacts with NO user logged in.
        mockMvc.perform(get("/contacts"))
                // Expect a redirect to the login page (302 or 3xx).
                .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @DisplayName("GET /contacts with user should succeed")
    @WithMockUser(username = "user1", roles = {"USER"})
    void contactsWithUserSucceeds() throws Exception {
        // This test simulates a logged-in user (username=user1, role=USER).
        mockMvc.perform(get("/contacts"))
        // expect a 200 OK, meaning the user can see the page.
        .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("GET / should be public (welcome page)")
    void rootIsPublic() throws Exception {
        // Anyone can access the root ("/")—no login required.
        mockMvc.perform(get("/"))
        .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("GET /login should be public")
    void loginIsPublic() throws Exception {
        // Anyone can access the login page.
        mockMvc.perform(get("/login"))
        .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/contacts with no credentials returns 401")
    void apiContactsNoCredentialsReturns401() throws Exception {
        // No credentials provided, expect 401 Unauthorized.
        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/contacts with invalid basic auth returns 401 (not 302)")
    void apiContactsInvalidBasicAuthReturns401() throws Exception {
        // Invalid credentials provided, expect 401 Unauthorized.
        mockMvc.perform(get("/api/contacts")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString("baduser:badpass".getBytes())))
                .andExpect(status().isUnauthorized());
    }
}
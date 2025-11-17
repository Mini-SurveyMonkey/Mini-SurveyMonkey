package org.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import; // Import this
import org.springframework.security.test.context.support.WithMockUser; // Import this
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Import your security configuration
@Import(org.example.configuration.WebSecurityConfig.class)
@WebMvcTest(ViewController.class)
class ViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void testHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @WithMockUser
    void testNewSurvey() throws Exception {
        mockMvc.perform(get("/surveys/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("surveys-create"));
    }
}
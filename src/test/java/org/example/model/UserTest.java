package org.example.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testDefaultConstructorInitializesFields() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNotNull(user.getSurveys()); 
        assertTrue(user.getSurveys().isEmpty());
    }

    @Test
    void testConstructorWithUsername() {
        User user = new User("tempUser");
        assertNull(user.getId());
        assertEquals("tempUser", user.getUsername());
        assertNotNull(user.getSurveys());
        assertTrue(user.getSurveys().isEmpty());
    }

    @Test
    void testGetAndSetId() {
        User user = new User();
        Long id = 1L;
        user.setId(id);
        assertEquals(id, user.getId());

        user.setId(null);
        assertNull(user.getId());

        Long id2 = 2L;
        user.setId(id2);
        assertEquals(id2, user.getId());
    }

    @Test
    void testGetAndSetUsername() {
        User user = new User();
        user.setUsername("alice");
        assertEquals("alice", user.getUsername());

        user.setUsername(null);
        assertNull(user.getUsername());

        user.setUsername("bob");
        assertEquals("bob", user.getUsername());
    }

    @Test
    void testGetAndSetSurveys() {
        User user = new User();

        List<Survey> surveys = new ArrayList<>();
        Survey s1 = new Survey();
        s1.setId(10L);
        s1.setTitle("Customer Survey");
        surveys.add(s1);

        user.setSurveys(surveys);
        assertSame(surveys, user.getSurveys());

        user.setSurveys(null);
        assertNull(user.getSurveys());

        List<Survey> surveys2 = Arrays.asList(new Survey(), new Survey());
        user.setSurveys(surveys2);
        assertSame(surveys2, user.getSurveys());
    }
}
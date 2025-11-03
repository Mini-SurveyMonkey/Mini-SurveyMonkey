package org.example.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SurveyTest {

    @Test
    void testDefaultConstructorInitializesFieldsToNull() {
        Survey survey = new Survey();
        assertNull(survey.getId());
        assertNull(survey.getTitle());
        assertFalse(survey.isClosed());
        assertNotNull(survey.getQuestions()); // because initialized in class
        assertTrue(survey.getQuestions().isEmpty());
    }

    @Test
    void testGetAndSetId() {
        Survey survey = new Survey();
        Long id = 1L;
        survey.setId(id);
        assertEquals(id, survey.getId());

        survey.setId(null);
        assertNull(survey.getId());

        Long id2 = 2L;
        survey.setId(id2);
        assertEquals(id2, survey.getId());
    }

    @Test
    void testGetAndSetTitle() {
        Survey survey = new Survey();
        survey.setTitle("Customer Feedback");
        assertEquals("Customer Feedback", survey.getTitle());

        survey.setTitle(null);
        assertNull(survey.getTitle());

        survey.setTitle("Employee Satisfaction");
        assertEquals("Employee Satisfaction", survey.getTitle());
    }

    @Test
    void testGetAndSetClosed() {
        Survey survey = new Survey();
        assertFalse(survey.isClosed());

        survey.setClosed(true);
        assertTrue(survey.isClosed());

        survey.setClosed(false);
        assertFalse(survey.isClosed());
    }

    @Test
    void testGetAndSetQuestions() {
        Survey survey = new Survey();

        List<Question> questions = new ArrayList<>();
        Question q1 = new Question();
        q1.setId(1L);
        q1.setQuestionText("Rate our service");
        questions.add(q1);

        survey.setQuestions(questions);
        assertSame(questions, survey.getQuestions());

        survey.setQuestions(null);
        assertNull(survey.getQuestions());

        List<Question> questions2 = Arrays.asList(new Question(), new Question());
        survey.setQuestions(questions2);
        assertSame(questions2, survey.getQuestions());
    }
}
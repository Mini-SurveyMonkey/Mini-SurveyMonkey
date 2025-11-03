package org.example.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void testDefaultConstructorInitializesFieldsToNull() {
        Question question = new Question();
        assertNull(question.getId());
        assertNull(question.getQuestionText());
        assertNull(question.getType());
        assertNull(question.getMinValue());
        assertNull(question.getMaxValue());
        assertNull(question.getOptions());
    }

    @Test
    void testGetAndSetId() {
        Question question = new Question();
        Long id = 1L;
        question.setId(id);
        assertEquals(id, question.getId());

        question.setId(null);
        assertNull(question.getId());

        Long id2 = 2L;
        question.setId(id2);
        assertEquals(id2, question.getId());
    }

    @Test
    void testGetAndSetQuestionText() {
        Question question = new Question();
        question.setQuestionText("What is your name?");
        assertEquals("What is your name?", question.getQuestionText());

        question.setQuestionText(null);
        assertNull(question.getQuestionText());

        question.setQuestionText("How old are you?");
        assertEquals("How old are you?", question.getQuestionText());
    }

    @Test
    void testGetAndSetType() {
        Question question = new Question();
        question.setType("Multiple Choice");
        assertEquals("Multiple Choice", question.getType());

        question.setType(null);
        assertNull(question.getType());

        question.setType("Rating");
        assertEquals("Rating", question.getType());
    }

    @Test
    void testGetAndSetMinValue() {
        Question question = new Question();
        Integer min = 1;
        question.setMinValue(min);
        assertEquals(min, question.getMinValue());

        question.setMinValue(null);
        assertNull(question.getMinValue());

        Integer min2 = 0;
        question.setMinValue(min2);
        assertEquals(min2, question.getMinValue());
    }

    @Test
    void testGetAndSetMaxValue() {
        Question question = new Question();
        Integer max = 5;
        question.setMaxValue(max);
        assertEquals(max, question.getMaxValue());

        question.setMaxValue(null);
        assertNull(question.getMaxValue());

        Integer max2 = 10;
        question.setMaxValue(max2);
        assertEquals(max2, question.getMaxValue());
    }

    @Test
    void testGetAndSetOptions() {
        Question question = new Question();

        List<String> options = new ArrayList<>();
        options.add("Yes");
        options.add("No");

        question.setOptions(options);
        assertSame(options, question.getOptions());

        question.setOptions(null);
        assertNull(question.getOptions());

        List<String> options2 = Arrays.asList("A", "B", "C");
        question.setOptions(options2);
        assertSame(options2, question.getOptions());
    }
}
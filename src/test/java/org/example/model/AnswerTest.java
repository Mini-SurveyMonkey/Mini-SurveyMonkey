package org.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnswerTest {

    @Test
    void testDefaultConstructorInitializesFieldsToNull(){
        Answer answer = new Answer();
        assertNull(answer.getId());
        assertNull(answer.getAnswerText());
        assertNull(answer.getQuestion());
        assertNull(answer.getSurvey());
    }

    @Test
    void testGetAndSetId() {
        Answer answer = new Answer();
        Long id = 1L;
        answer.setId(id);
        assertEquals(id, answer.getId());

        answer.setId(null);
        assertNull(answer.getId());

        Long id2 = 2L;
        answer.setId(id2);
        assertEquals(id2, answer.getId());
    }

    @Test
    void testGetAndSetQuestion() {
        Question question = new Question();

        Question question2 = new Question();

        Answer answer = new Answer();

        answer.setQuestion(question);

        assertSame(question, answer.getQuestion());

        answer.setQuestion(null);

        assertNull(answer.getQuestion());

        answer.setQuestion(question2);

        assertSame(question2, answer.getQuestion());
    }

    @Test
    void testGetAndSetSurvey() {
        Survey survey = new Survey();

        Survey survey2 = new Survey();

        Answer answer = new Answer();

        answer.setSurvey(survey);

        assertSame(survey, answer.getSurvey());

        answer.setSurvey(null);

        assertNull(answer.getSurvey());

        answer.setSurvey(survey2);

        assertSame(survey2, answer.getSurvey());
    }

    @Test
    void testGetAndSetAnswerText() {
        Answer answer = new Answer();
        answer.setAnswerText("answer");
        assertEquals("answer", answer.getAnswerText());

        answer.setAnswerText(null);

        assertNull(answer.getAnswerText());

        answer.setAnswerText("answer2");
        assertEquals("answer2", answer.getAnswerText());
    }
}
package org.example.repository;

import org.example.model.Question;
import org.example.model.Survey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class tests in isolation using an H2 in-memory database
 */
@DataJpaTest
class SurveyRepositoryTest {

    @Autowired
    private SurveyRepository surveyRepository;

    @Test
    void testSaveAndFindById() {
        // Arrange
        Survey survey = new Survey();
        survey.setTitle("Customer Feedback");

        Question question = new Question();
        question.setQuestionText("How satisfied are you?");
        survey.getQuestions().add(question);

        // Act
        Survey savedSurvey = surveyRepository.save(survey);
        Optional<Survey> foundSurvey = surveyRepository.findById(savedSurvey.getId());

        // Assert
        assertThat(foundSurvey).isPresent();
        assertThat(foundSurvey.get().getTitle()).isEqualTo("Customer Feedback");
        assertThat(foundSurvey.get().getQuestions()).hasSize(1);
        assertThat(foundSurvey.get().getQuestions().get(0).getQuestionText())
                .isEqualTo("How satisfied are you?");
    }

    @Test
    void testExistsById() {
        // Arrange
        Survey survey = new Survey();
        survey.setTitle("Existence Test");
        Survey saved = surveyRepository.save(survey);

        // Act & Assert
        assertThat(surveyRepository.existsById(saved.getId())).isTrue();
        assertThat(surveyRepository.existsById(-999L)).isFalse();
    }

    @Test
    void testDeleteById() {
        // Arrange
        Survey survey = new Survey();
        survey.setTitle("To be deleted");
        Survey saved = surveyRepository.save(survey);

        // Act
        surveyRepository.deleteById(saved.getId());

        // Assert
        assertThat(surveyRepository.findById(saved.getId())).isNotPresent();
    }

    @Test
    void testCount() {
        // Arrange
        long initialCount = surveyRepository.count();

        Survey survey1 = new Survey();
        survey1.setTitle("Survey 1");
        surveyRepository.save(survey1);

        Survey survey2 = new Survey();
        survey2.setTitle("Survey 2");
        surveyRepository.save(survey2);

        // Act & Assert
        assertThat(surveyRepository.count()).isEqualTo(initialCount + 2);
    }
}
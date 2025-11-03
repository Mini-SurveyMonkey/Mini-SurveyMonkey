package org.example.repository;

import org.example.model.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class tests in isolation using an H2 in-memory database
 */
@DataJpaTest
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    void testSaveAndFindById() {
        // Arrange
        Question question = new Question();
        question.setQuestionText("What is your favorite color?");
        question.setType("Multiple Choice");

        // Act
        Question savedQuestion = questionRepository.save(question);
        Optional<Question> foundQuestion = questionRepository.findById(savedQuestion.getId());

        // Assert
        assertThat(foundQuestion).isPresent();
        assertThat(foundQuestion.get().getQuestionText()).isEqualTo("What is your favorite color?");
        assertThat(foundQuestion.get().getType()).isEqualTo("Multiple Choice");
    }

    @Test
    void testExistsById() {
        // Arrange
        Question question = new Question();
        question.setQuestionText("Existence check");
        Question saved = questionRepository.save(question);

        // Act & Assert
        assertThat(questionRepository.existsById(saved.getId())).isTrue();
        assertThat(questionRepository.existsById(-999L)).isFalse();
    }

    @Test
    void testDeleteById() {
        // Arrange
        Question question = new Question();
        question.setQuestionText("To be deleted");
        Question saved = questionRepository.save(question);

        // Act
        questionRepository.deleteById(saved.getId());

        // Assert
        assertThat(questionRepository.findById(saved.getId())).isNotPresent();
    }

    @Test
    void testCount() {
        // Arrange
        long initialCount = questionRepository.count();

        Question question1 = new Question();
        question1.setQuestionText("Question 1");
        questionRepository.save(question1);

        Question question2 = new Question();
        question2.setQuestionText("Question 2");
        questionRepository.save(question2);

        // Act & Assert
        assertThat(questionRepository.count()).isEqualTo(initialCount + 2);
    }
}

package org.example.repository;

import org.example.model.Answer;
import org.example.model.Question;
import org.example.model.Survey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class tests in isolation using an in-memory database
 */
@DataJpaTest
class AnswerRepositoryTest {

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    void testSaveAndFindById() {
        // Arrange
        Answer answer = new Answer();
        answer.setAnswerText("Sample answer");

        Question question = new Question();
        question.setQuestionText("Test question");
        answer.setQuestion(question);

        Survey survey = new Survey();
        survey.setTitle("Test survey");
        answer.setSurvey(survey);

        // Act
        Answer savedAnswer = answerRepository.save(answer);
        Optional<Answer> foundAnswer = answerRepository.findById(savedAnswer.getId());

        // Assert
        assertThat(foundAnswer).isPresent();
        assertThat(foundAnswer.get().getAnswerText()).isEqualTo("Sample answer");
        assertThat(foundAnswer.get().getQuestion().getQuestionText()).isEqualTo("Test question");
        assertThat(foundAnswer.get().getSurvey().getTitle()).isEqualTo("Test survey");
    }

    @Test
    void testExistsById() {
        // Arrange
        Answer answer = new Answer();
        answer.setAnswerText("Answer for existence check");
        Answer saved = answerRepository.save(answer);

        // Act & Assert
        assertThat(answerRepository.existsById(saved.getId())).isTrue();
        assertThat(answerRepository.existsById(-999L)).isFalse();
    }

    @Test
    void testDeleteById() {
        // Arrange
        Answer answer = new Answer();
        answer.setAnswerText("To be deleted");
        Answer saved = answerRepository.save(answer);

        // Act
        answerRepository.deleteById(saved.getId());

        // Assert
        assertThat(answerRepository.findById(saved.getId())).isNotPresent();
    }

    @Test
    void testCount() {
        // Arrange
        long initialCount = answerRepository.count();

        Answer answer1 = new Answer();
        answer1.setAnswerText("Answer 1");
        answerRepository.save(answer1);

        Answer answer2 = new Answer();
        answer2.setAnswerText("Answer 2");
        answerRepository.save(answer2);

        // Act & Assert
        assertThat(answerRepository.count()).isEqualTo(initialCount + 2);
    }
}
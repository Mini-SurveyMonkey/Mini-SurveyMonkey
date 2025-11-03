package org.example;

import org.example.controller.SurveyController;
import org.example.controller.ViewController;
import org.example.repository.AnswerRepository;
import org.example.repository.QuestionRepository;
import org.example.repository.SurveyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to ensure that all components are created in the application context
 */
@SpringBootTest
public class GeneralTest {
    @Autowired
    private SurveyController surveyController;
    @Autowired
    private ViewController viewController;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionRepository questionRepository;

    @Test
    void contextLoads() throws Exception {
        Object[] contextComponents = {
                surveyController,
                viewController,
                surveyRepository,
                answerRepository,
                questionRepository
        };

        for (Object o : contextComponents) {
            assertThat(o).isNotNull();
        }
    }
}
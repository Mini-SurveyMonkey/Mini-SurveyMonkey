package org.example.controller;

import org.example.model.Answer;
import org.example.model.Question;
import org.example.model.Survey;
import org.example.model.User;
import org.example.repository.AnswerRepository;
import org.example.repository.QuestionRepository;
import org.example.repository.SurveyRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SurveyController.class)
class SurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SurveyRepository surveyRepository;

    @MockitoBean
    private QuestionRepository questionRepository;

    @MockitoBean
    private AnswerRepository answerRepository;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User temp = new User("temp");
        when(userRepository.findByUsername("temp")).thenReturn(Optional.of(temp));
    }
    
    @Test
    void testCreateSurvey() throws Exception {
        Survey inputSurvey = new Survey();
        inputSurvey.setTitle("New Survey");

        Survey savedSurvey = new Survey();
        savedSurvey.setId(1L);
        savedSurvey.setTitle("New Survey");

        when(surveyRepository.save(any(Survey.class))).thenReturn(savedSurvey);

        mockMvc.perform(post("/surveys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Survey\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Survey"));

        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    void testAddQuestion() throws Exception {
        Long surveyId = 1L;
        Question inputQuestion = new Question();
        inputQuestion.setQuestionText("Sample question");

        Survey existingSurvey = new Survey();
        existingSurvey.setId(surveyId);
        existingSurvey.setTitle("Existing Survey");
        existingSurvey.setQuestions(new ArrayList<>());

        Survey updatedSurvey = new Survey();
        updatedSurvey.setId(surveyId);
        updatedSurvey.setTitle("Existing Survey");
        updatedSurvey.setQuestions(Arrays.asList(inputQuestion));

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(existingSurvey));
        when(surveyRepository.save(any(Survey.class))).thenReturn(updatedSurvey);

        mockMvc.perform(post("/surveys/{surveyId}/questions", surveyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionText\":\"Sample question\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(surveyId))
                .andExpect(jsonPath("$.questions[0].questionText").value("Sample question"));

        verify(surveyRepository).findById(surveyId);
        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    void testSubmitAnswer() throws Exception {
        Long surveyId = 1L;
        Long questionId = 2L;
        Answer inputAnswer = new Answer();
        inputAnswer.setAnswerText("Sample answer");

        Answer savedAnswer = new Answer();
        savedAnswer.setId(10L);
        savedAnswer.setAnswerText("Sample answer");

        when(answerRepository.save(any(Answer.class))).thenReturn(savedAnswer);

        mockMvc.perform(post("/surveys/{surveyId}/questions/{questionId}/answers", surveyId, questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answerText\":\"Sample answer\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.answerText").value("Sample answer"));

        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    void testGetSurvey() throws Exception {
        Long id = 1L;
        Survey survey = new Survey();
        survey.setId(id);
        survey.setTitle("Test Survey");

        when(surveyRepository.findById(id)).thenReturn(Optional.of(survey));

        mockMvc.perform(get("/surveys/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Test Survey"));

        verify(surveyRepository).findById(id);
    }

    @Test
    void testGetAllSurveys() throws Exception {
        Survey survey1 = new Survey();
        survey1.setId(1L);
        survey1.setTitle("Survey 1");

        Survey survey2 = new Survey();
        survey2.setId(2L);
        survey2.setTitle("Survey 2");

        List<Survey> surveys = Arrays.asList(survey1, survey2);

        when(surveyRepository.findAll()).thenReturn(surveys);

        mockMvc.perform(get("/surveys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Survey 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Survey 2"));

        verify(surveyRepository).findAll();
    }

    @Test
    void testCloseSurvey() throws Exception {
        Long id = 1L;
        Survey existingSurvey = new Survey();
        existingSurvey.setId(id);
        existingSurvey.setTitle("Open Survey");
        existingSurvey.setClosed(false);

        Survey closedSurvey = new Survey();
        closedSurvey.setId(id);
        closedSurvey.setTitle("Open Survey");
        closedSurvey.setClosed(true);

        when(surveyRepository.findById(id)).thenReturn(Optional.of(existingSurvey));
        when(surveyRepository.save(any(Survey.class))).thenReturn(closedSurvey);

        mockMvc.perform(post("/surveys/{id}/close", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.closed").value(true));

        verify(surveyRepository).findById(id);
        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    void testToggleSurvey() throws Exception {
        Long id = 1L;

        Survey openSurvey = new Survey();
        openSurvey.setId(id);
        openSurvey.setTitle("Toggle Survey");
        openSurvey.setClosed(false);

        Survey closedSurvey = new Survey();
        closedSurvey.setId(id);
        closedSurvey.setTitle("Toggle Survey");
        closedSurvey.setClosed(true);

        when(surveyRepository.findById(id)).thenReturn(Optional.of(openSurvey));
        when(surveyRepository.save(any(Survey.class))).thenReturn(closedSurvey);

        mockMvc.perform(post("/surveys/{id}/close", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.closed").value(true));

        Survey reopened = new Survey();
        reopened.setId(id);
        reopened.setTitle("Toggle Survey");
        reopened.setClosed(false);

        when(surveyRepository.findById(id)).thenReturn(Optional.of(closedSurvey));
        when(surveyRepository.save(any(Survey.class))).thenReturn(reopened);

        mockMvc.perform(post("/surveys/{id}/close", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.closed").value(false));

        verify(surveyRepository, times(2)).findById(id);
        verify(surveyRepository, times(2)).save(any(Survey.class));
    }

    @Test
    void testReturnShareableLink() throws Exception {
        Long surveyId = 1L;

        mockMvc.perform(get("/surveys/{id}/share", surveyId))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/surveys/" + surveyId + "/fill")));
    }

    @Test
    void testDisplayFillableSurvey() throws Exception {
        Long surveyId = 2L;
        Survey survey = new Survey();
        survey.setId(surveyId);
        survey.setTitle("Fillable Survey");

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        mockMvc.perform(get("/surveys/{id}/fill", surveyId))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Fillable Survey")));

        verify(surveyRepository).findById(surveyId);
    }
}
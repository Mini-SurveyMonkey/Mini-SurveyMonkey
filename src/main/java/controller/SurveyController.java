package controller;

import model.Answer;
import model.Question;
import model.Survey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import repository.AnswerRepository;
import repository.QuestionRepository;
import repository.SurveyRepository;

import java.util.List;

@RestController
public class SurveyController {
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @PostMapping("/surveys")
    public Survey createSurvey(@RequestBody Survey survey) { return surveyRepository.save(survey); }

    @PostMapping("/surveys/{surveyId}/questions")
    public Survey addQuestion(@PathVariable Long surveyId, @RequestBody Question question) {
        Survey survey = surveyRepository.findById(surveyId).orElseThrow();
        survey.getQuestions().add(question);
        return surveyRepository.save(survey);
    }

    @PostMapping("/surveys/{surveyId}/questions/{questionId}/answers")
    public Answer submitAnswer(@PathVariable Long surveyId, @PathVariable Long questionId, @RequestBody Answer answer) {
        return answerRepository.save(answer);
    }

    @GetMapping("/surveys/{id}")
    public Survey getSurvey(@PathVariable Long id) { return surveyRepository.findById(id).orElseThrow(); }

    @GetMapping("/surveys")
    public List<Survey> getAllSurveys() { return (List<Survey>) surveyRepository.findAll(); }

    @PostMapping("/surveys/{id}/close")
    public Survey closeSurvey(@PathVariable Long id) {
        Survey survey = surveyRepository.findById(id).orElseThrow();
        survey.setClosed(true);
        return surveyRepository.save(survey);
    }
}


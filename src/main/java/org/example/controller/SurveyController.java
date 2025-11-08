package org.example.controller;

import org.example.model.*;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class SurveyController {
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/surveys")
    public Survey createSurvey(@RequestBody Survey survey) { 
        User temp = userRepository.findByUsername("temp")
        .orElseGet(() -> userRepository.save(new User("temp")));

        survey.setCreator(temp);

        return surveyRepository.save(survey); 
    }

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
    public Survey closeOrOpenSurvey(@PathVariable Long id) {
        Survey survey = surveyRepository.findById(id).orElseThrow();
        survey.setClosed(!survey.isClosed());
        return surveyRepository.save(survey);
    }

    @GetMapping("/surveys/{surveyId}/share")
    public String getShareableSurveyLink(@PathVariable Long surveyId, HttpServletRequest request) {
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return baseUrl + "/surveys/" + surveyId + "/fill";
    }

    @GetMapping("/surveys/{surveyId}/fill")
    public ModelAndView showSurveyToFill(@PathVariable Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        ModelAndView mav = new ModelAndView("survey-fill"); // This is the name of your Thymeleaf template
        mav.addObject("survey", survey);
        return mav;
    }
}


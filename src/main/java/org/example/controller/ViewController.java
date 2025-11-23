package org.example.controller;

import org.example.model.Survey;
import org.example.repository.SurveyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    private final SurveyRepository surveyRepository;

    public ViewController(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/surveys/new")
    public String newSurvey() {
        return "surveys-create";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/surveys/{id}/response")
    public String takeSurvey(@PathVariable Long id, Model model) {
        Survey survey = surveyRepository.findById(id).orElseThrow();
        model.addAttribute("closed", survey.isClosed());
        model.addAttribute("surveyId", id);
        return "surveys-take";
    }

    @GetMapping("/surveys/{id}/preview")
    public String previewSurvey(@PathVariable Long id, Model model) {
        model.addAttribute("surveyId", id);
        model.addAttribute("preview", true);
        return "surveys-preview";
    }
}

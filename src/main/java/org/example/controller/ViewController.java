package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/surveys/new")
    public String newSurvey() {
        return "surveys-create";
    }

    @GetMapping("/surveys/{id}/take")
    public String takeSurvey(@PathVariable Long id, Model model) {
        model.addAttribute("surveyId", id);
        return "surveys-take";
    }
}

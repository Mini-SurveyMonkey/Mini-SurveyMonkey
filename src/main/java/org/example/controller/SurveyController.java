package org.example.controller;

import org.example.model.*;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

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
    public Answer submitAnswer(@PathVariable Long surveyId,
                               @PathVariable Long questionId,
                               @RequestBody Answer answer) {
        Survey survey = surveyRepository.findById(surveyId).orElseThrow();
        Question question = questionRepository.findById(questionId).orElseThrow();

        // Link the answer to its survey and question
        answer.setSurvey(survey);
        answer.setQuestion(question);

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
        return baseUrl + "/surveys/" + surveyId + "/response";
    }

    @DeleteMapping("/surveys/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        surveyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // NEW: aggregated results for charts
    @GetMapping("/api/surveys/{surveyId}/results")
    public Map<String, Object> getSurveyResults(@PathVariable Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId).orElseThrow();

        Map<String, Object> result = new HashMap<>();
        result.put("title", survey.getTitle());

        List<Map<String, Object>> questionResults = new ArrayList<>();

        for (Question q : survey.getQuestions()) {
            Map<String, Object> qMap = new HashMap<>();
            qMap.put("questionId", q.getId());
            qMap.put("questionText", q.getQuestionText());
            qMap.put("type", q.getType()); // "NUMBER", "CHOICE", etc.

            List<Answer> answers =
                    answerRepository.findBySurveyIdAndQuestionId(surveyId, q.getId());

            if ("NUMBER".equalsIgnoreCase(q.getType())) {
                qMap.put("bins", computeNumberBinsSimple(q, answers));
            } else if (q.getType().toUpperCase().startsWith("CHOICE")) {
                qMap.put("counts", computeChoiceCountsSimple(answers));
            }

            questionResults.add(qMap);
        }

        result.put("questions", questionResults);
        return result;
    }

    // Helpers for NUMBER questions: build bins list [{label, count}, ...]
    private List<Map<String, Object>> computeNumberBinsSimple(Question q, List<Answer> answers) {
        List<Map<String, Object>> bins = new ArrayList<>();

        if (answers.isEmpty()) {
            return bins;
        }

        int min = (q.getMinValue() != null) ? q.getMinValue() : 0;
        int max = (q.getMaxValue() != null) ? q.getMaxValue() : 10;

        int range = Math.max(1, max - min + 1);
        int binCount = 5;
        int binSize = Math.max(1, range / binCount);

        long[] counts = new long[binCount];

        for (Answer a : answers) {
            try {
                int v = Integer.parseInt(a.getAnswerText());
                int idx = (v - min) / binSize;
                if (idx < 0) idx = 0;
                if (idx >= binCount) idx = binCount - 1;
                counts[idx]++;
            } catch (NumberFormatException ignored) {
            }
        }

        for (int i = 0; i < binCount; i++) {
            int from = min + i * binSize;
            int to = (i == binCount - 1) ? max : (from + binSize - 1);

            Map<String, Object> bin = new HashMap<>();
            bin.put("label", from + "–" + to);
            bin.put("count", counts[i]);
            bins.add(bin);
        }

        return bins;
    }

    // Helpers for CHOICE questions: build { option -> count }
    private Map<String, Long> computeChoiceCountsSimple(List<Answer> answers) {
        Map<String, Long> map = new HashMap<>();
        for (Answer a : answers) {
            String val = a.getAnswerText();
            if (val == null || val.isBlank()) continue;
            map.put(val, map.getOrDefault(val, 0L) + 1);
        }
        return map;
    }

}


package org.example.repository;

import org.example.model.Answer;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface AnswerRepository extends CrudRepository<Answer, Long> {

    List<Answer> findBySurveyIdAndQuestionId(Long surveyId, Long questionId);

}


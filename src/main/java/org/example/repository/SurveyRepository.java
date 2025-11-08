package org.example.repository;

import org.example.model.Survey;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface SurveyRepository extends CrudRepository<Survey, Long> {
    List<Survey> findByCreatorId(Long userId);
}


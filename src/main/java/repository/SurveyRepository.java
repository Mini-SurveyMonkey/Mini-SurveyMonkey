package repository;

import model.Survey;
import org.springframework.data.repository.CrudRepository;

public interface SurveyRepository extends CrudRepository<Survey, Long> {}


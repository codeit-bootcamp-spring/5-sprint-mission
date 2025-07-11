package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Survey;

import java.util.List;
import java.util.UUID;

public interface SurveyService {
    void createSurvey(Survey survey);

    Survey findById(UUID id);

    List<Survey> findAll();

    void updateQuestion(Survey survey, String question);

    void updateAnswers(Survey survey, List<String> answers);

    void updateDuration(Survey survey, long duration);

    void updateIsDuplicateResponseAllowed(Survey survey, boolean isDuplicateResponseAllowed);

    void deleteById(UUID id);
}

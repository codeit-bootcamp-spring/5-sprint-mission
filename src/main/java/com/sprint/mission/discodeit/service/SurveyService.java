package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Survey;

import java.util.List;

public interface SurveyService extends Service<Survey> {
    boolean createSurvey(Survey survey);

    void updateQuestion(Survey survey, String question);

    void updateAnswers(Survey survey, List<String> answers);

    void updateDuration(Survey survey, long duration);

    void updateDuplicateResponseAllowed(Survey survey, boolean isDuplicateResponseAllowed);
}

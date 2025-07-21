package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Survey;
import java.util.UUID;

public interface SurveyService extends BaseService<Survey> {
  void updateClosed(UUID surveyId, boolean isClosed);

  void vote(UUID surveyId, int answerIndex, UUID voterId, boolean isUnvoted);
}

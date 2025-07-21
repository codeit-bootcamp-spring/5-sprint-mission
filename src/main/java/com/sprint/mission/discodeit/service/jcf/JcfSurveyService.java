package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Survey;
import com.sprint.mission.discodeit.service.SurveyService;
import java.util.UUID;

public class JcfSurveyService extends BaseJcfService<Survey> implements SurveyService {
  private static final JcfSurveyService instance = new JcfSurveyService();

  private JcfSurveyService() {}

  public static JcfSurveyService getInstance() {
    return instance;
  }

  @Override
  public Survey save(Survey survey) {
    if (findById(survey.getId()).isPresent()) {
      throw new IllegalArgumentException("중복된 id가 존재합니다.");
    }
    data.add(survey);
    return survey;
  }

  @Override
  public void updateClosed(UUID surveyId, boolean isClosed) {
    update(surveyId, s -> s.setClosed(isClosed));
  }

  @Override
  public void vote(UUID surveyId, int answerIndex, UUID voterId, boolean isUnvoted) {
    data.stream()
        .filter(s -> s.getId().equals(surveyId))
        .findFirst()
        .ifPresent(s -> s.vote(answerIndex, voterId, isUnvoted));
  }
}

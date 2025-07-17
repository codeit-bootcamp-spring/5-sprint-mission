package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Survey;
import com.sprint.mission.discodeit.service.SurveyService;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class JcfSurveyService extends JcfService<Survey> implements SurveyService {
  private static final JcfSurveyService instance = new JcfSurveyService();

  private JcfSurveyService() {}

  public static JcfSurveyService getInstance() {
    return instance;
  }

  @Override
  protected boolean idEquals(Survey survey, UUID id) {
    return survey.getId().equals(id);
  }

  @Override
  public boolean createSurvey(Survey survey) {
    boolean exists = data.stream().anyMatch(s -> s.getId().equals(survey.getId()));
    if (exists) {
      System.out.println("중복된 id가 존재합니다.");
      return false;
    }
    data.add(survey);
    return true;
  }

  @Override
  public void update(UUID surveyId, Consumer<Survey> updater) {
    Survey s = findById(surveyId);
    if (s != null) {
      updater.accept(s);
      s.setUpdatedAt(System.currentTimeMillis());
    }
  }

  @Override
  public void updateQuestion(Survey survey, String question) {
    data.stream()
        .filter(s -> s.getId().equals(survey.getId()))
        .findFirst()
        .ifPresent(
            s -> {
              s.setQuestion(question);
              s.setUpdatedAt(System.currentTimeMillis());
            });
  }

  @Override
  public void updateAnswers(Survey survey, List<String> answers) {
    data.stream()
        .filter(s -> s.getId().equals(survey.getId()))
        .findFirst()
        .ifPresent(
            s -> {
              s.setAnswers(answers);
              s.setUpdatedAt(System.currentTimeMillis());
            });
  }

  @Override
  public void updateDuration(Survey survey, long duration) {
    data.stream()
        .filter(s -> s.getId().equals(survey.getId()))
        .findFirst()
        .ifPresent(
            s -> {
              s.setDuration(duration);
              s.setUpdatedAt(System.currentTimeMillis());
            });
  }

  @Override
  public void updateDuplicateResponseAllowed(Survey survey, boolean isDuplicateResponseAllowed) {
    data.stream()
        .filter(s -> s.getId().equals(survey.getId()))
        .findFirst()
        .ifPresent(
            s -> {
              s.setDuplicateResponseAllowed(isDuplicateResponseAllowed);
              s.setUpdatedAt(System.currentTimeMillis());
            });
  }
}

package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Survey;
import com.sprint.mission.discodeit.service.SurveyService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFSurveyService implements SurveyService {
    private static final JCFSurveyService instance = new JCFSurveyService();

    private final List<Survey> data;

    private JCFSurveyService() {
        data = new ArrayList<Survey>();
    }

    public static JCFSurveyService getInstance() {
        return instance;
    }

    @Override
    public void createSurvey(Survey survey) {
        boolean exists = data.stream()
                .anyMatch(s -> s.getId().equals(survey.getId()));
        if (exists) {
            System.out.println("중복된 id가 존재합니다.");
            return;
        }
        data.add(survey);
    }

    @Override
    public Survey findById(UUID id) {
        return data.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Survey> findAll() {
        return data;
    }

    @Override
    public void updateQuestion(Survey survey, String question) {
        data.stream()
                .filter(s -> s.getId().equals(survey.getId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setQuestion(question);
                    s.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateAnswers(Survey survey, List<String> answers) {
        data.stream()
                .filter(s -> s.getId().equals(survey.getId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setAnswers(answers);
                    s.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateDuration(Survey survey, long duration) {
        data.stream()
                .filter(s -> s.getId().equals(survey.getId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setDuration(duration);
                    s.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateIsDuplicateResponseAllowed(Survey survey, boolean isDuplicateResponseAllowed) {
        data.stream()
                .filter(s -> s.getId().equals(survey.getId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setDuplicateResponseAllowed(isDuplicateResponseAllowed);
                    s.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void deleteById(UUID id) {
        data.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .ifPresent(data::remove);
    }
}

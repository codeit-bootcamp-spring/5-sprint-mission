package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.utility.StringUtil;
import com.sprint.mission.discodeit.utility.Validators;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private final UUID senderId;
  private String content;
  private List<String> files;
  private Survey survey;
  private final UUID replyTo;

  public Message(UUID senderId, String content, List<String> files, Survey survey, UUID replyTo) {
    if (senderId == null) {
      throw new IllegalArgumentException("Sender ID must not be null.");
    }
    this.senderId = senderId;
    setContent(content);
    setFiles(files);
    setSurvey(survey);
    this.replyTo = replyTo;
  }

  public Message(UUID senderId, String content, List<String> files, Survey survey) {
    this(senderId, content, files, survey, null);
  }

  public Message(UUID senderId, String content, List<String> files) {
    this(senderId, content, files, null, null);
  }

  public Message(UUID senderId, String content) {
    this(senderId, content, null, null, null);
  }

  public UUID getSenderId() {
    return senderId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    if (content == null) {
      throw new IllegalArgumentException("Content must not be null.");
    }
    this.content = StringUtil.normalizeString(content);
  }

  public List<String> getFiles() {
    return Collections.unmodifiableList(files);
  }

  public void setFiles(List<String> files) {
    if (files == null) {
      this.files = new ArrayList<>();
    } else {
      for (String file : files) {
        Validators.validateUri(file);
      }
      this.files = new ArrayList<>(files);
    }
  }

  public Survey getSurvey() {
    return survey;
  }

  public void setSurvey(Survey survey) {
    if (survey == null) {
      this.survey = null;
    } else {
      if (!survey.getSenderId().equals(senderId)) {
        throw new IllegalStateException("Survey Sender ID is not equal to message senderId.");
      }
      this.survey = survey;
    }
  }

  public UUID getReplyTo() {
    return replyTo;
  }

  @Override
  public String toString() {
    return "Message{"
        + "sender="
        + senderId
        + ", content='"
        + content
        + '\''
        + ", files="
        + files
        + ", survey="
        + survey
        + '}';
  }
}

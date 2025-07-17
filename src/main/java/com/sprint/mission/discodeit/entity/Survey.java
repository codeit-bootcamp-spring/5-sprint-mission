package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Survey extends AbstractBaseEntity {
  private final long createdAt;
  private long updatedAt;
  private final UUID senderId;
  private final String question;
  private final long durationMillis;
  private final boolean isDuplicateResponseAllowed;
  private boolean isClosed;
  private final List<String> answers;
  private final List<Integer> voteCounts;
  private final List<Set<UUID>> voters;

  public Survey(
      UUID senderId,
      String question,
      long durationMillis,
      boolean isDuplicateResponseAllowed,
      List<String> answers) {
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
    this.senderId = senderId;
    this.question = question;
    this.durationMillis = durationMillis;
    this.isDuplicateResponseAllowed = isDuplicateResponseAllowed;

    this.answers = answers == null ? Collections.emptyList() : new ArrayList<>(answers) {};
    this.voteCounts = new ArrayList<>(this.answers.size());
    this.voters = new ArrayList<>(this.answers.size());
    for (int i = 0; i < this.answers.size(); i++) {
      this.voteCounts.add(0);
      this.voters.add(new HashSet<>());
    }
  }

  private boolean isInvalidAnswersIndex(int answerIndex) {
    return answerIndex < 0 || answerIndex >= answers.size();
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(long updatedAt) {
    this.updatedAt = updatedAt;
  }

  public UUID getSenderId() {
    return senderId;
  }

  public String getQuestion() {
    return question;
  }

  public long getDurationMillis() {
    return durationMillis;
  }

  public boolean isDuplicateResponseAllowed() {
    return isDuplicateResponseAllowed;
  }

  public boolean isClosed() {
    return isClosed;
  }

  public void setClosed(boolean closed) {
    isClosed = closed;
  }

  public List<String> getAnswers() {
    return Collections.unmodifiableList(answers);
  }

  public List<Integer> getVoteCounts() {
    return Collections.unmodifiableList(voteCounts);
  }

  public List<Set<UUID>> getVoters() {
    return Collections.unmodifiableList(voters);
  }

  public boolean vote(int answerIndex, UUID voterId) {
    if (isInvalidAnswersIndex(answerIndex)) {
      return false;
    }
    Set<UUID> voterSet = voters.get(answerIndex);
    if (voterSet.add(voterId)) {
      voteCounts.set(answerIndex, voteCounts.get(answerIndex) + 1);
      return true;
    }
    return false;
  }

  public boolean unvote(int answerIndex, UUID voterId) {
    if (isInvalidAnswersIndex(answerIndex)) {
      return false;
    }
    Set<UUID> voterSet = voters.get(answerIndex);
    if (voterSet.remove(voterId)) {
      voteCounts.set(answerIndex, voteCounts.get(answerIndex) - 1);
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "Survey{"
        + "id="
        + this.getId()
        + "createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + ", senderId="
        + senderId
        + ", question='"
        + question
        + '\''
        + ", durationMillis="
        + durationMillis
        + ", isDuplicateResponseAllowed="
        + isDuplicateResponseAllowed
        + ", isClosed="
        + isClosed
        + ", answers="
        + answers
        + ", voteCounts="
        + voteCounts
        + ", voters="
        + voters
        + '}';
  }
}

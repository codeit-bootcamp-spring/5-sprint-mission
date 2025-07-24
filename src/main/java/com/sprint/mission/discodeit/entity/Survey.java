package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Survey extends BaseEntity {
  private final UUID senderId;
  private final String question;
  private final long durationMillis;
  private final boolean duplicateResponseAllowed;
  private boolean closed;
  private final List<String> answers;
  private final List<Integer> voteCounts;
  private final List<Set<UUID>> voters;

  public Survey(
      UUID senderId,
      String question,
      long durationMillis,
      boolean duplicateResponseAllowed,
      List<String> answers) {
    this.senderId = senderId;
    if (question == null || question.isBlank()) {
      throw new IllegalArgumentException("이런, 뭔가를 잊으신 것 같아요. 질문을 추가해주세요.");
    }
    this.question = question;
    if (durationMillis < 0) {
      throw new IllegalArgumentException("지속 시간은 음수일 수 없습니다.");
    }
    this.durationMillis = durationMillis;
    this.duplicateResponseAllowed = duplicateResponseAllowed;
    if (answers == null || answers.isEmpty()) {
      throw new IllegalArgumentException("이런, 뭔가를 잊으신 것 같아요. 답변은 하나 이상 추가해주세요.");
    }
    this.answers = new ArrayList<>(answers);
    this.voteCounts = new ArrayList<>(this.answers.size());
    this.voters = new ArrayList<>(this.answers.size());
    for (int i = 0; i < this.answers.size(); i++) {
      this.voteCounts.add(0);
      this.voters.add(new HashSet<>());
    }
  }

  private boolean isIndexOutOfBounds(int answerIndex) {
    return answerIndex < 0 || answerIndex >= answers.size();
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
    return duplicateResponseAllowed;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }

  public List<String> getAnswers() {
    return Collections.unmodifiableList(answers);
  }

  public List<Integer> getVoteCounts() {
    return Collections.unmodifiableList(voteCounts);
  }

  public List<Set<UUID>> getVoters() {
    List<Set<UUID>> safe = new ArrayList<>(voters.size());
    for (Set<UUID> set : voters) {
      safe.add(Collections.unmodifiableSet(set));
    }
    return Collections.unmodifiableList(safe);
  }

  public void vote(int answerIndex, UUID voterId) {
    if (System.currentTimeMillis() - getUpdatedAt() > durationMillis) {
      setClosed(true);
    }
    if (closed) {
      throw new IllegalStateException("설문이 만료되었습니다.");
    }
    if (duplicateResponseAllowed) {
      throw new IllegalArgumentException("이 설문은 복수 선택만 허용합니다. vote(List<Integer>, ...)를 사용하세요.");
    }
    if (isIndexOutOfBounds(answerIndex)) {
      throw new IllegalArgumentException("Invalid answer index: " + answerIndex);
    }
    for (Set<UUID> voterSet : voters) {
      if (voterSet.contains(voterId)) {
        throw new IllegalArgumentException("투표 삭제 후 재투표해 주세요.");
      }
    }
    Set<UUID> voterSet = voters.get(answerIndex);
    voterSet.add(voterId);
    voteCounts.set(answerIndex, voteCounts.get(answerIndex) + 1);
  }

  public void vote(List<Integer> answerIndices, UUID voterId) {
    if (System.currentTimeMillis() - getUpdatedAt() > durationMillis) {
      setClosed(true);
    }
    if (closed) {
      throw new IllegalStateException("설문이 만료되었습니다.");
    }
    if (!duplicateResponseAllowed) {
      throw new IllegalArgumentException("이 설문은 단일 선택만 허용합니다. vote(int, ...)를 사용하세요.");
    }
    if (answerIndices == null || answerIndices.isEmpty()) {
      throw new IllegalArgumentException("선택한 답변이 없습니다.");
    }

    Set<Integer> uniqueIndices = new HashSet<>(answerIndices);

    for (int idx : uniqueIndices) {
      if (isIndexOutOfBounds(idx)) {
        throw new IllegalArgumentException("Invalid answer index: " + idx);
      }
    }

    for (Set<UUID> voterSet : voters) {
      if (voterSet.contains(voterId)) {
        throw new IllegalArgumentException("투표 삭제 후 재투표해 주세요.");
      }
    }

    for (int idx : uniqueIndices) {
      Set<UUID> voterSet = voters.get(idx);
      voterSet.add(voterId);
      voteCounts.set(idx, voteCounts.get(idx) + 1);
    }
  }

  public void unvote(UUID voterId) {
    for (int i = 0; i < voters.size(); i++) {
      Set<UUID> voterSet = voters.get(i);
      if (voterSet.remove(voterId)) {
        voteCounts.set(i, voteCounts.get(i) - 1);
      }
    }
  }

  @Override
  public String toString() {
    return "Survey{"
        + ", senderId="
        + senderId
        + ", question='"
        + question
        + '\''
        + ", durationMillis="
        + durationMillis
        + ", duplicateResponseAllowed="
        + duplicateResponseAllowed
        + ", closed="
        + closed
        + ", answers="
        + answers
        + ", voteCounts="
        + voteCounts
        + ", voters="
        + voters
        + '}';
  }
}

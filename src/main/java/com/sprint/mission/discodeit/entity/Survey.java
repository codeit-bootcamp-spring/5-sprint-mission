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
    this.question = question;
    this.durationMillis = durationMillis;
    this.duplicateResponseAllowed = duplicateResponseAllowed;

    this.answers = answers == null ? Collections.emptyList() : new ArrayList<>(answers);
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

  public void vote(int answerIndex, UUID voterId, boolean isUnvoted) {
    if (closed) {
      throw new IllegalStateException("투표가 마감되었습니다.");
    }

    if (isIndexOutOfBounds(answerIndex)) {
      throw new IllegalArgumentException("잘못된 투표 인덱스 : " + answerIndex);
    }

    Set<UUID> voterSet = voters.get(answerIndex);

    if (isUnvoted) {
      if (voterSet.remove(voterId)) {
        voteCounts.set(answerIndex, voteCounts.get(answerIndex) - 1);
      } else {
        throw new IllegalArgumentException(
            "Voter " + voterId + " has not voted for answer " + answerIndex);
      }
      return;
    }

    if (duplicateResponseAllowed) {
      if (!voterSet.add(voterId)) {
        throw new IllegalArgumentException(
            "Voter " + voterId + " has already voted for answer " + answerIndex);
      }
      voteCounts.set(answerIndex, voteCounts.get(answerIndex) + 1);
    } else {
      int prevIndex = -1;
      for (int i = 0; i < voters.size(); i++) {
        if (voters.get(i).contains(voterId)) {
          prevIndex = i;
          break;
        }
      }
      if (prevIndex == answerIndex) {
        throw new IllegalArgumentException(
            "Voter " + voterId + " has already voted for answer " + answerIndex);
      }

      if (prevIndex != -1) {
        voters.get(prevIndex).remove(voterId);
        voteCounts.set(prevIndex, voteCounts.get(prevIndex) - 1);
      }

      voterSet.add(voterId);
      voteCounts.set(answerIndex, voteCounts.get(answerIndex) + 1);
    }
  }

  @Override
  public String toString() {
    return "Survey{"
        + "id="
        + getId()
        + ", createdAt="
        + getCreatedAt()
        + ", updatedAt="
        + getUpdatedAt()
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

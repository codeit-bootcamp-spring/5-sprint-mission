package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.exception.ValidatorsValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validators {

  private static final int MAX_BIO_LENGTH = 190;

  private static final int MIN_GUILD_NAME_LENGTH = 2;
  private static final int MAX_GUILD_NAME_LENGTH = 100;

  private static final int MIN_CHANNEL_NAME_LENGTH = 1;
  private static final int MAX_CHANNEL_NAME_LENGTH = 100;

  private static final int MIN_PHONE_DIGITS = 10;
  private static final int MAX_PHONE_DIGITS = 13;

  private static final int MAX_MESSAGE_CONTENT_LENGTH = 2000;

  public static String validateChannelName(String channelName) {
    requireLengthBetween(channelName, MIN_CHANNEL_NAME_LENGTH, MAX_CHANNEL_NAME_LENGTH,
        MIN_CHANNEL_NAME_LENGTH + "자에서 " + MAX_CHANNEL_NAME_LENGTH + "자 사이여야 해요.");
    return channelName;
  }

  public static String validateMessageContent(String content) {
    if (content == null) {
      return null;
    }
    String normalized = content.strip();
    if (normalized.isEmpty()) {
      throw new ValidatorsValidationException("내용은 비워둘 수 없습니다.");
    }
    int cpLen = normalized.codePointCount(0, normalized.length());
    if (cpLen > MAX_MESSAGE_CONTENT_LENGTH) {
      throw new ValidatorsValidationException(
          "메시지는 최대 " + MAX_MESSAGE_CONTENT_LENGTH + "자까지 가능합니다. 현재: " + cpLen + "자");
    }
    return normalized;
  }

  private static void requireLengthBetween(String s, int min, int max, String messageIfInvalid) {
    if (s == null) {
      throw new ValidatorsValidationException(messageIfInvalid);
    }
    int len = s.length();
    if (len < min || len > max) {
      throw new ValidatorsValidationException(messageIfInvalid);
    }
  }
}

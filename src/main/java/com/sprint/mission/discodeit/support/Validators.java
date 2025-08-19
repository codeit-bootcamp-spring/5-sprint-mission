package com.sprint.mission.discodeit.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validators {

  private static final int MAX_BIO_LENGTH = 190;

  private static final int MIN_GUILD_NAME_LENGTH = 2;
  private static final int MAX_GUILD_NAME_LENGTH = 100;

  private static final int MIN_PHONE_DIGITS = 10;
  private static final int MAX_PHONE_DIGITS = 13;

  private static final int MAX_MESSAGE_CONTENT_LENGTH = 2000;
}

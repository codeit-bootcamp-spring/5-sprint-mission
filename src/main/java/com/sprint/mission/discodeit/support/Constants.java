package com.sprint.mission.discodeit.support;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

  public static final List<String> SUPPORTED_IMAGE_TYPE = List.of(
      MediaType.APPLICATION_OCTET_STREAM_VALUE,
      MediaType.IMAGE_PNG_VALUE,
      MediaType.IMAGE_JPEG_VALUE,
      "image/webp"
  );

  public static final int MAX_MESSAGE_CONTENT_LENGTH = 2000;

  public static final int MAX_BIO_LENGTH = 190;

  public static final int MIN_GUILD_NAME_LENGTH = 2;
  public static final int MAX_GUILD_NAME_LENGTH = 100;

  public static final int MIN_PHONE_DIGITS = 10;
  public static final int MAX_PHONE_DIGITS = 13;

}

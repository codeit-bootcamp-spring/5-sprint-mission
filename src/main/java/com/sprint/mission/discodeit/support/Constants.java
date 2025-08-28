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

    public static final int MIN_USERNAME_LENGTH = 2;
    public static final int MAX_USERNAME_LENGTH = 50;

    public static final int MIN_EMAIL_LENGTH = 6;
    public static final int MAX_EMAIL_LENGTH = 100;

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 72;

    public static final int MIN_CHANNEL_NAME_LENGTH = 1;
    public static final int MAX_CHANNEL_NAME_LENGTH = 100;

    public static final int MAX_CHANNEL_DESCRIPTION_LENGTH = 1024;

    public static final int MIN_CHANNEL_PARTICIPANTS = 2;
    public static final int MAX_CHANNEL_PARTICIPANTS = 10;

    public static final int MIN_MESSAGE_CONTENT_LENGTH = 1;
    public static final int MAX_MESSAGE_CONTENT_LENGTH = 2000;

    public static final int MAX_MESSAGE_ATTACHMENTS = 10;

    public static final int MAX_BIO_LENGTH = 190;

    public static final int MIN_GUILD_NAME_LENGTH = 2;
    public static final int MAX_GUILD_NAME_LENGTH = 100;

    public static final int MIN_PHONE_DIGITS = 10;
    public static final int MAX_PHONE_DIGITS = 13;
}

package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

public class MessageDto {

  @Getter
  public static class MessageCreateRequest {

    UUID channelId;
    UUID authorId;
    String content;
  }

  @Getter
  @Builder
  public static class CreateRequest {

    UUID channelId;
    UUID authorId;
    String content;
    List<MultipartFile> attachments;
  }

  @Getter
  @Builder
  public static class UpdateRequest {

    @Setter
    UUID id;
    String text;
  }

  @Getter
  @Builder
  @ToString
  public static class DetailResponse {

    UUID channelId;
    UUID authorId;
    UUID id;
    String authorName;
    String channelName;
    String content;
    List<UUID> attachmentIds;
    Instant createdAt;
    Instant updatedAt;
  }
}

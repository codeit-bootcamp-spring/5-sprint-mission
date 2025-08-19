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
  public static class CreateRequest {

    UUID channelId;
    UUID authorId;
    String content;

    public Create toCreate(List<MultipartFile> attachments) {
      return Create.builder()
          .channelId(this.channelId)
          .authorId(this.authorId)
          .content(this.content)
          .attachments(attachments)
          .build();
    }
  }

  @Getter
  @Builder
  public static class Create {

    UUID channelId;
    UUID authorId;
    String content;
    List<MultipartFile> attachments;
  }

  @Getter
  public static class UpdateRequest {

    String content;

    public Update toUpdate(UUID id) {
      return Update.builder().id(id).content(this.content).build();
    }
  }

  @Getter
  @Builder
  public static class Update {

    @Setter
    UUID id;
    String content;
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

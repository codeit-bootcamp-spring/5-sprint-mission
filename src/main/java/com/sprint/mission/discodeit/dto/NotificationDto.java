package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

public class NotificationDto {

  @Getter
  @Builder
  public static class Detail {

    private UUID id;
    private UserDto.Detail receiver;
    private String title;
    private String content;
    private String type;
    private Instant createdAt;
  }


  @Getter
  @Builder
  public static class DetailResponse {

    private UUID id;
    private UserDto.DetailResponse receiver;
    private String title;
    private String content;
    private String type;
    private Instant createdAt;
  }
}

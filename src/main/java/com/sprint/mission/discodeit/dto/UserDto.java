package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

public class UserDto {

  @Getter
  @Builder
  public static class CreateRequest {

    private String username;
    private String email;
    private String password;
    private MultipartFile profileImage;
  }

  @Getter
  public static class UserRequest {

    private String username;
    private String email;
    private String password;
  }

  @Getter
  @Builder
  public static class UpdateRequest {

    @Setter
    private UUID id;
    private String username;
    private String email;
    private String password;
    private MultipartFile profileImage;
  }

  @Getter
  @Builder
  public static class SummaryResponse {

    private UUID id;
    private String username;
    private String email;
  }

  @Getter
  @Builder
  @ToString
  public static class DetailResponse {

    private UUID id;
    private String username;
    private String email;
    private UUID profileId;
    private Boolean online;
    private Instant createdAt;
    private Instant updatedAt;
  }
}

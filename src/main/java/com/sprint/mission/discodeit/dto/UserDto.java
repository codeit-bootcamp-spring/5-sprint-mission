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
  public static class Create {

    private String username;
    private String email;
    private String password;
    private MultipartFile profileImage;
  }

  @Getter
  public static class Request {

    private String username;
    private String email;
    private String password;

    public Create toCreate(MultipartFile profileImage) {
      return Create.builder()
          .username(this.username)
          .email(this.email)
          .password(this.password)
          .profileImage(profileImage)
          .build();
    }

    public Update toUpdate(UUID id, MultipartFile profileImage) {
      return Update.builder()
          .id(id)
          .username(this.username)
          .email(this.email)
          .password(this.password)
          .profileImage(profileImage)
          .build();
    }
  }

  @Getter
  @Builder
  public static class Update {

    @Setter
    private UUID id;
    private String username;
    private String email;
    private String password;
    private MultipartFile profileImage;
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

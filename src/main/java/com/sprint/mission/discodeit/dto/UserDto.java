package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class UserDto {

  @Getter
  public static class CreateRequest {

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
  }

  @Getter
  @Builder
  public static class Create {

    private String username;
    private String email;
    private String password;
    private MultipartFile profileImage;
  }

  @Getter
  public static class UpdateRequest {

    private String newUsername;
    private String newEmail;
    private String newPassword;

    public Update toUpdate(UUID id, MultipartFile profileImage) {
      return Update.builder()
          .id(id)
          .username(this.newUsername)
          .email(this.newEmail)
          .password(this.newPassword)
          .profileImage(profileImage)
          .build();
    }
  }

  @Getter
  @Builder
  public static class Update {

    private UUID id;
    private String username;
    private String email;
    private String password;
    private MultipartFile profileImage;
  }

  @Getter
  @Builder
  public static class DetailResponse {

    private UUID id;
    private String username;
    private String email;
    private UUID profileId;
    private Boolean online;
    private Instant createdAt;
    private Instant updatedAt;
  }

  @Builder
  public static class Detail {

    private UUID id;
    private String username;
    private String email;
    private UUID profileId;
    private Boolean online;
    private Instant createdAt;
    private Instant updatedAt;

    public DetailResponse toDetailResponse() {
      return DetailResponse.builder()
          .id(id)
          .username(username)
          .email(email)
          .profileId(profileId)
          .online(online)
          .createdAt(createdAt)
          .updatedAt(updatedAt)
          .build();
    }
  }
}

package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class UserDto {

  @Getter
  @Schema(name = "UserCreateRequest")
  public static class CreateRequest {

    private String username;
    private String email;
    private String password;

    public CreateCommand toCommand(MultipartFile profileImage) {
      return CreateCommand.builder()
                          .username(this.username)
                          .email(this.email)
                          .password(this.password)
                          .profileImage(profileImage)
                          .build();
    }
  }

  @Getter
  @Builder
  public static class CreateCommand {

    private String username;
    private String email;
    private String password;
    private MultipartFile profileImage;
  }

  @Getter
  @Schema(name = "UserUpdateRequest")
  public static class UpdateRequest {

    private String newUsername;
    private String newEmail;
    private String newPassword;

    public UpdateCommand toCommand(UUID id, MultipartFile profileImage) {
      return UpdateCommand.builder()
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
  public static class UpdateCommand {

    private UUID id;
    private String username;
    private String email;
    private String password;
    private MultipartFile profileImage;
  }

  @Getter
  @Builder
  @Schema(name = "UserDetailResponse")
  public static class DetailResponse {

    private UUID id;
    private String username;
    private String email;
    private BinaryContentDto.DetailResponse profile;
    private Boolean online;
  }

  @Getter
  @Builder
  public static class Detail {

    private UUID id;
    private String username;
    private String email;
    private BinaryContentDto.Detail profile;
    @Setter
    private Boolean online;

  }
}

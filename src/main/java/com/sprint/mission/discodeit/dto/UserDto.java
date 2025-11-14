package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class UserDto {

  @Getter
  @Builder
  @Schema(name = "UserCreateRequest")
  public static class CreateRequest {

    @NotBlank(message = "사용자명은 필수 입력값입니다.")
    private String username;
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
  }

  @Getter
  @Builder
  public static class CreateCommand {

    private String username;
    private String email;
    private String password;
    private MultipartFile profileImage;
    private String role;
  }

  @Getter
  @Builder
  @Schema(name = "UserUpdateRequest")
  public static class UpdateRequest {

    private String newUsername;
    private String newEmail;
    private String newPassword;
    private String newRole;
  }

  @Getter
  @Builder
  public static class UpdateRoleRequest {

    private UUID id;
    private String newRole;
  }

  @Getter
  @Builder
  public static class UpdateCommand {

    private UUID id;
    private String username;
    private String email;
    private String password;
    private MultipartFile profileImage;
    private String role;
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
    private String role;
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
    private String role;

  }
}

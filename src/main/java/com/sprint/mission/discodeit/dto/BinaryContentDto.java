package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class BinaryContentDto {

  @Getter
  @Builder
  @Schema(name = "BinaryContentCreateRequest")
  public static class CreateRequest {

    // TODO 추후 요구 조건에 맞춰서 변경 필요할듯?
    MultipartFile file;

    public CreateCommand toCommand() {
      try {
        return CreateCommand.builder()
                            .fileName(this.file.getOriginalFilename())
                            .bytes(this.file.getBytes())
                            .contentType(this.file.getContentType())
                            .size(this.file.getSize())
                            .build();
      } catch (Exception e) {
        // TODO 예외처리
        return null;
      }
    }
  }

  @Getter
  @Builder
  @AllArgsConstructor
  public static class CreateCommand {

    String fileName;
    String contentType;
    Long size;
    byte[] bytes;

    public CreateCommand(MultipartFile file) {
      try {
        this.fileName = file.getOriginalFilename();
        this.contentType = file.getContentType();
        this.size = file.getSize();
        this.bytes = file.getBytes();
      } catch (Exception e) {
        // TODO 예외처리
      }
    }
  }

  @Getter
  @Builder
  @Schema(name = "BinaryContentDetailResponse")
  public static class DetailResponse {

    UUID id;
    String fileName;
    String contentType;
    Long size;
    byte[] bytes;
  }

  @Getter
  @Builder
  public static class Detail {

    UUID id;
    String fileName;
    String contentType;
    Long size;
    byte[] bytes;
  }
}

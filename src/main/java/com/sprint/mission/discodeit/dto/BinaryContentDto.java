package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class BinaryContentDto {

  @Getter
  @Builder
  public static class Create {

    // TODO 추후 요구 조건에 맞춰서 변경 필요할듯?
    MultipartFile file;
  }

  @Getter
  @Builder
  @Schema(name = "BinaryContentDetailResponse")
  public static class DetailResponse {

    UUID id;
    String name;
    String contentType;
    Long size;
    Instant createdAt;
    byte[] bytes;
  }

  @Getter
  @Builder
  public static class Detail {

    UUID id;
    String name;
    String contentType;
    Long size;
    Instant createdAt;
    byte[] bytes;

    public DetailResponse toResponse() {
      return DetailResponse.builder().id(id).name(name).contentType(contentType).size(size)
          .createdAt(createdAt).bytes(bytes).build();
    }
  }
}

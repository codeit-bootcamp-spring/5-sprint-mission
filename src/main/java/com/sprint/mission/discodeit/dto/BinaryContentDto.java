package com.sprint.mission.discodeit.dto;

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
  public static class DetailResponse {

    UUID id;
    String name;
    String contentType;
    Long size;
    Instant createdAt;
    byte[] bytes;
  }
}

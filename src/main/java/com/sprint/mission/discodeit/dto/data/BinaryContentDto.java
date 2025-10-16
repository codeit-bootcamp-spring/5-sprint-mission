package com.sprint.mission.discodeit.dto.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;
import lombok.Data;

@Data
public class BinaryContentDto {

  private String key; // S3 key 값

  private UUID id;

  @NotNull(message = "파일 데이터는 필수입니다.")
  private byte[] data; // 인코딩된 파일 내용

  @NotBlank(message = "파일 이름은 필수입니다.")
  private String fileName; // 파일 이름

  @PositiveOrZero(message = "파일 크기는 0 이상이어야 합니다.")
  private long size; // 파일 크기 (단위: 바이트)

  @NotBlank(message = "파일 타입은 필수입니다.")
  private String contentType; // 파일 타입 (예: image/png)
}
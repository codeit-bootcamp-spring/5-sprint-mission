package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;
import lombok.Data;

@Data
public class BinaryContentDto {

  private UUID id;
  private byte[] data; // 인코딩된 파일 내용
  private String fileName; // 파일 이름
  private long size; // 파일 크기 (단위: 바이트)
  private String contentType; // 파일 타입 (예: image/png)
}
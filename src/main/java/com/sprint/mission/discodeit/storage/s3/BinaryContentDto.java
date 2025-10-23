package com.sprint.mission.discodeit.storage.s3;

import java.util.UUID;

public class BinaryContentDto {
  private UUID id;
  private String filename;
  private String contentType;

  public BinaryContentDto(UUID id, String filename, String contentType) {
    this.id = id;
    this.filename = filename;
    this.contentType = contentType;
  }
  public UUID getId() { return id; }
  public String getFilename() { return filename; }
  public String getContentType() { return contentType; }
}

package com.sprint.mission.discodeit.dto;

public record ProfileImageParam(
      byte[] bytes,
      String filename,
      String fileType
) {
}

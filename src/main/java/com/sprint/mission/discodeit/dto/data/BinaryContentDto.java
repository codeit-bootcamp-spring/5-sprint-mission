package com.sprint.mission.discodeit.dto.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BinaryContentDto(UUID id, String fileName, Long size, String contentType) {

}

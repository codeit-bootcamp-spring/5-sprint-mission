package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BinaryContentDTO {
    private UUID id;
    private String fileName;
    private String contentType;
    private Long size;
    private BinaryContentStatus status;
}

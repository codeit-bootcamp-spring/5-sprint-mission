package com.sprint.mission.discodeit.dto.binarycontent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BinaryContentCreateRequest {
    private byte[] data;
    private String contentType;
    private long size;
    private UUID ownerId;
    private String fileName;

}

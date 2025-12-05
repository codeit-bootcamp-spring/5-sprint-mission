package com.sprint.mission.discodeit.domain.binarycontent.service;

import com.sprint.mission.discodeit.domain.binarycontent.dto.data.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface BinaryContentStorage {

    UUID put(UUID binaryContentId, byte[] bytes);

    ResponseEntity<?> download(BinaryContentDto metaData);
}

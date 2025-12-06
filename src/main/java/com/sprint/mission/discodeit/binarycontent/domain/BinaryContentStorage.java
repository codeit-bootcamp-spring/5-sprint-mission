package com.sprint.mission.discodeit.binarycontent.domain;

import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface BinaryContentStorage {

    UUID put(UUID binaryContentId, byte[] bytes);

    ResponseEntity<Void> download(BinaryContentDto metaData);
}

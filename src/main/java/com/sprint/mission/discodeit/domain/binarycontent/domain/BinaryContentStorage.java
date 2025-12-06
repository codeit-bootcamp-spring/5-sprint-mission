package com.sprint.mission.discodeit.domain.binarycontent.domain;

import com.sprint.mission.discodeit.domain.binarycontent.presentation.dto.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface BinaryContentStorage {

    UUID put(UUID binaryContentId, byte[] bytes);

    ResponseEntity<Void> download(BinaryContentDto metaData);
}

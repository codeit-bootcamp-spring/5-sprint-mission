package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface BinaryContentStorage {

    UUID put(UUID binaryContentId, byte[] bytes);

    ResponseEntity<?> download(BinaryContentDto metaData);
}

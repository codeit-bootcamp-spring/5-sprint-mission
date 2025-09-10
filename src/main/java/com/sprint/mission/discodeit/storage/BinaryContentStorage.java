package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface BinaryContentStorage {
    UUID put(UUID uuid, List<Byte> bite);

    InputStream get(UUID uuid);

    ResponseEntity<?> download(BinaryContentDto binaryContentDto);
}

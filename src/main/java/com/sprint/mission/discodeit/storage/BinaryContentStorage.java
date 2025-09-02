package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;

public interface BinaryContentStorage {

    UUID put(UUID id, byte[] bytes);

    InputStream get(UUID id);

    byte[] download(BinaryContentDto binaryContentDto);
}

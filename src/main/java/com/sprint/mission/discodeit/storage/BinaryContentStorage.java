package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.core.io.Resource;

public interface BinaryContentStorage {

  UUID put(UUID id, byte[] bytes);

  InputStream get(UUID id);

  Resource download(BinaryContentDto dto);
}

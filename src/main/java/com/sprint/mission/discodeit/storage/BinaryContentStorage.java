package com.sprint.mission.discodeit.storage;

import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.UUID;

public interface BinaryContentStorage {

    UUID put(UUID binaryContentId, byte[] bytes);

    InputStream get(UUID binaryContentId);

    Resource getResource(UUID binaryContentId);
}

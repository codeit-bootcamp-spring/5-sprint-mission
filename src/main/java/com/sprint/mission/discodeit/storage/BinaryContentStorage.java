package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

public interface BinaryContentStorage {
  UUID put(UUID id, byte[] bytes) throws IOException;
  InputStream get(UUID id) throws IOException;
  ResponseEntity<Resource> download(BinaryContentDto dto) throws IOException;
}
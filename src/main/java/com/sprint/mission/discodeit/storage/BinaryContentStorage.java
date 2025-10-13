package com.sprint.mission.discodeit.storage;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;

public interface BinaryContentStorage {

	UUID put(UUID id, byte[] bytes, MediaType contentType);

	InputStream get(UUID id);

	ResponseEntity<?> download(BinaryContentDto dto);
}

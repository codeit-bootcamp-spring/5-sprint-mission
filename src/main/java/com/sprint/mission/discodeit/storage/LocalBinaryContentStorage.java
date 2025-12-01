package com.sprint.mission.discodeit.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.configuration.LocalStorageProps;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.storage.StorageFileMissingException;
import com.sprint.mission.discodeit.exception.storage.StorageInitException;
import com.sprint.mission.discodeit.exception.storage.StorageReadException;
import com.sprint.mission.discodeit.exception.storage.StorageWriteException;
import com.sprint.mission.discodeit.log.LogUtils;
import com.sprint.mission.discodeit.service.BinaryContentService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local", matchIfMissing = true)
@Slf4j
public class LocalBinaryContentStorage implements BinaryContentStorage {

	private final Path root;
	private final BinaryContentService binaryContentService;

	LocalBinaryContentStorage(LocalStorageProps props, BinaryContentService binaryContentService) {
		root = Paths.get(props.getRootPath());
		this.binaryContentService = binaryContentService;
	}

	@PostConstruct
	private void init() {
		if (Files.notExists(root)) {
			try {
				Files.createDirectories(root);
			} catch (IOException e) {
				throw new StorageInitException(e).addDetail("root", root);
			}
		}
	}

	private Path resolvePath(UUID id) {
		return root.resolve(id.toString());
	}

	@Override
	public UUID put(UUID id, byte[] bytes) {
		log.debug("[LocalBinaryContentStorage#put] try with id: {}", id);
		Path path = resolvePath(id);
		try {
			if (Files.notExists(path)) {
				Files.write(path, bytes);
			}
		} catch (IOException e) {
			binaryContentService.updateStatus(id, BinaryContentStatus.FAIL);
			throw new StorageWriteException(e).addDetail("path", path);
		}
		log.info("[LocalBinaryContentStorage#put] file uploaded: id={}, filename={}", id,
			path.getFileName());
		binaryContentService.updateStatus(id, BinaryContentStatus.SUCCESS);

		return id;
	}

	@Override
	public InputStream get(UUID id) {
		Path path = resolvePath(id);
		try {
			return Files.newInputStream(path);
		} catch (IOException e) {
			throw new StorageReadException(e).addDetail("path", path);
		}
	}

	@Override
	public ResponseEntity<Resource> download(BinaryContentDto dto) {
		log.debug("[LocalBinaryContentStorage#download] try: {}", LogUtils.summarizeAttachment(dto));
		Path path = resolvePath(dto.id());

		if (Files.notExists(path)) {
			throw new StorageFileMissingException().addDetail("path", path);
		}

		Resource resource = new InputStreamResource(get(dto.id()));
		log.info("[LocalBinaryContentStorage#download] downloaded: {}",
			LogUtils.summarizeAttachment(dto));

		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(dto.contentType()))
			.contentLength(dto.size())
			.header(HttpHeaders.CONTENT_DISPOSITION,
				ContentDisposition
					.attachment()
					.filename(dto.fileName(), StandardCharsets.UTF_8)
					.build()
					.toString())
			.body(resource);
	}
}

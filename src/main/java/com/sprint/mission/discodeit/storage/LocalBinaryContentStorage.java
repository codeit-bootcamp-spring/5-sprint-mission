package com.sprint.mission.discodeit.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Slf4j
public class LocalBinaryContentStorage implements BinaryContentStorage {

	@Value("${discodeit.storage.local.root-path}")
	private String rootPath;
	private Path root;

	// Bean 생성 시 호출
	@PostConstruct
	public void init() throws IOException {
		root = Paths.get(rootPath);
		if (!Files.exists(root)) {
			Files.createDirectories(root);
		}
	}

	// UUID 기반 파일 경로
	private Path resolvePath(UUID id) {
		return root.resolve(id.toString());
	}

	@Override
	public UUID put(UUID id, byte[] bytes) {
		try {
			Path path = resolvePath(id);

			if (bytes == null) {
				// null 이면 삭제 처리
				Files.deleteIfExists(path);
				return id;
			}
			Files.write(path, bytes);
			return id;
		} catch (IOException e) {
			throw new RuntimeException("Failed to save file: " + id, e);
		}
	}

	@Override
	public InputStream get(UUID id) {
		try {
			Path path = resolvePath(id);
			if (!Files.exists(path)) {
				throw new NoSuchElementException("BinaryContent with ID" + id + "not found");
			}
			return Files.newInputStream(path);
		} catch (IOException e) {
			throw new RuntimeException("Fail to read BinaryContent with ID" + id);
		}
	}

	@Override
	public ResponseEntity<Resource> download(BinaryContentDto dto) {
		log.debug("download BinaryContent with ID={} In Storage start", dto.getId());
		try {
			Path path = resolvePath(dto.getId());
			if (!Files.exists(path)) {
				log.error("badRequest : BinaryContent with ID={} not found", dto.getId());
				throw new NoSuchElementException("BinaryContent with ID" + dto.getId() + "not found");
			}
			Resource resource = new UrlResource(path.toUri());
			String contentDisposition = "attachment; filename=\"" + dto.getFileName() + "\"";

			log.debug("download success BinaryContent with ID={}", dto.getId());
			return ResponseEntity.ok()
			  .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
			  .contentType(MediaType.parseMediaType(dto.getContentType()))
			  .body(resource);
		} catch (MalformedURLException e) {
			log.error("Failed to create UrlResource for BinaryContent with ID={}, fileName={}",
			  dto.getId(), dto.getFileName(), e);

			throw new RuntimeException(e);
		}
	}
}

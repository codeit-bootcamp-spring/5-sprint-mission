package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.log.LogUtils;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@Slf4j
public class BinaryContentController {

	private final BinaryContentService binaryContentService;
	private final BinaryContentStorage binaryContentStorage;

	@GetMapping("/{binaryContentId}")
	public ResponseEntity<BinaryContentDto> find(
		@PathVariable("binaryContentId") UUID binaryContentId) {
		BinaryContentDto dto = binaryContentService.findById(binaryContentId);

		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

	@GetMapping
	public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
		@RequestParam List<UUID> binaryContentIds) {
		List<BinaryContentDto> binaryContentDtos = binaryContentService.findAllByIdIn(binaryContentIds);

		return ResponseEntity.status(HttpStatus.OK).body(binaryContentDtos);
	}

	@GetMapping("/{binaryContentId}/download")
	public ResponseEntity<Resource> download(@PathVariable UUID binaryContentId) {
		log.debug("Downloading binary content: id={}", binaryContentId);

		BinaryContentDto dto = binaryContentService.findById(binaryContentId);
		ResponseEntity<Resource> resource = binaryContentStorage.download(dto);
		log.info("Downloaded binary content: {}", LogUtils.summarizeAttachment(dto));

		return resource;
	}
}

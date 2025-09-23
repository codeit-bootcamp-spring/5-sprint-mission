package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.domain.dto.FindBiContentsIdInDTO;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent", description = "첨부파일 API")
@Slf4j
public class BinaryContentController {

	private final BinaryContentService binaryContentService;
	private final BinaryContentStorage binaryContentStorage;
	private final BinaryContentMapper binaryContentMapper;

	@GetMapping
	public ResponseEntity<List<BinaryContentResponse>> getBinaryContents(
	  @RequestParam @Size(min = 1) List<UUID> binaryContentIds) {

		List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(FindBiContentsIdInDTO.builder()
		  .ids(binaryContentIds).build());

		return ResponseEntity.ok(binaryContents.stream().map(binaryContentMapper::toResponse).toList());
	}

	@GetMapping("/{id}")
	public ResponseEntity<BinaryContentResponse> getBinaryContents(@PathVariable @NotNull UUID id) {
		BinaryContentDto result = binaryContentService.find(id);

		return ResponseEntity.ok(binaryContentMapper.toResponse(result));
	}

	@GetMapping("/{binaryContentId}/download")
	public ResponseEntity<?> downloadContent(@PathVariable UUID binaryContentId) {
		log.debug("Request to download BinaryContent id={}", binaryContentId);

		BinaryContentDto dto = binaryContentService.find(binaryContentId);
		log.debug("download BinaryContent Request successfully done id={}", binaryContentId);
		return binaryContentStorage.download(dto);
	}
}

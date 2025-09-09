package com.sprint.mission.discodeit.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.domain.dto.CreateReadStatusDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateReadStatusDTO;
import com.sprint.mission.discodeit.domain.dto.readStatus.ReadStatusDto;
import com.sprint.mission.discodeit.domain.dto.readStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.domain.request.CreateReadStatusRequest;
import com.sprint.mission.discodeit.domain.request.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.service.ReadStatusService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {

	private final ReadStatusService readStatusService;
	private final ReadStatusMapper readStatusMapper;

	@PostMapping
	public ResponseEntity<ReadStatusResponse> createReadStatus(
	  @RequestBody @Valid CreateReadStatusRequest request) {
		ReadStatusDto newReadStatuses = readStatusService.create(CreateReadStatusDTO.builder()
		  .userId(request.getUserId())
		  .channelId(request.getChannelId())
		  .lastReadAt(request.getLastReadAt())
		  .build());

		return ResponseEntity.status(CREATED).body(readStatusMapper.toResponse(newReadStatuses));
	}

	@PatchMapping("/{readStatusId}")
	public ResponseEntity<ReadStatusResponse> updateReadStatus(
	  @PathVariable UUID readStatusId,
	  @RequestBody @Valid UpdateReadStatusRequest request) {
		ReadStatusDto updatedReadStatuses = readStatusService.update(
		  UpdateReadStatusDTO.builder().id(readStatusId).newLastReadAt(request.getNewLastReadAt()).build());

		return ResponseEntity.ok(readStatusMapper.toResponse(updatedReadStatuses));
	}

	@GetMapping
	public ResponseEntity<List<ReadStatusResponse>> getReadStatusesByUserId(
	  @RequestParam UUID userId) {
		List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);
		return ResponseEntity.ok(readStatuses.stream().map(readStatusMapper::toResponse).toList());
	}

}

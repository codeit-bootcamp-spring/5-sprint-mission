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
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
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
	public ResponseEntity<ReadStatusDto> createReadStatus(
	  @RequestBody @Valid CreateReadStatusRequest request) {
		ReadStatus newReadStatuses = readStatusService.create(CreateReadStatusDTO.builder()
		  .userId(request.getUserId())
		  .channelId(request.getChannelId())
		  .lastReadAt(request.getLastReadAt())
		  .build());

		return ResponseEntity.status(CREATED).body(readStatusMapper.toDto(newReadStatuses));
	}

	@PatchMapping("/{readStatusId}")
	public ResponseEntity<ReadStatusDto> updateReadStatus(
	  @PathVariable UUID readStatusId,
	  @RequestBody @Valid UpdateReadStatusRequest request) {
		ReadStatus updatedReadStatuses = readStatusService.update(
		  UpdateReadStatusDTO.builder().id(readStatusId).newLastReadAt(request.getNewLastReadAt()).build());

		return ResponseEntity.ok(readStatusMapper.toDto(updatedReadStatuses));
	}

	@GetMapping
	public ResponseEntity<List<ReadStatusDto>> getReadStatusesByUserId(
	  @RequestParam UUID userId) {
		List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
		return ResponseEntity.ok(readStatuses.stream().map(readStatusMapper::toDto).toList());
	}

}

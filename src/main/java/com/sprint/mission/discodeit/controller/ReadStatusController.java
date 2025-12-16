package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

	private final ReadStatusService readStatusService;

	@PostMapping
	public ResponseEntity<ReadStatusDto> create(
		@RequestBody @Valid ReadStatusCreateRequest readStatusCreateRequest) {
		ReadStatusDto readStatusDto = readStatusService.create(readStatusCreateRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(readStatusDto);
	}

	@PatchMapping("/{readStatusId}")
	public ResponseEntity<ReadStatusDto> update(
		@PathVariable UUID readStatusId,
		@RequestBody @Valid ReadStatusUpdateRequest readStatusUpdateRequest) {
		ReadStatusDto readStatusDto = readStatusService.update(readStatusId, readStatusUpdateRequest);
		return ResponseEntity.status(HttpStatus.OK).body(readStatusDto);
	}

	@GetMapping
	public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId) {
		List<ReadStatusDto> readStatusDtos = readStatusService.findAllByUserId(userId);
		return ResponseEntity.status(HttpStatus.OK).body(readStatusDtos);
	}
}

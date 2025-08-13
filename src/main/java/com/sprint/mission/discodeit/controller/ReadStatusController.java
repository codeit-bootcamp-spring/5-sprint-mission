package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.request.readStatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.dto.response.readStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/readstatus")
@RequiredArgsConstructor
public class ReadStatusController {
	private final ReadStatusService readStatusService;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<ReadStatusResponse> createReadStatus(@RequestBody CreateReadStatusRequest request) {
		ReadStatusResponse response = readStatusService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<ReadStatusResponse>> getReadStatusByUser(@RequestParam UUID userId) {
		List<ReadStatusResponse> responses = readStatusService.getAllByUserId(userId);
		return ResponseEntity.ok(responses);
	}

	@RequestMapping(path = "/update", method = RequestMethod.GET)
	public ResponseEntity<ReadStatusResponse> updateReadStatus(@ModelAttribute UpdateReadStatusRequest request) {
		ReadStatusResponse response = readStatusService.updateByChannelIdAndUserId(request);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<ReadStatusResponse> deleteReadStatus(@PathVariable UUID id) {
		ReadStatusResponse response = readStatusService.delete(id);
		return ResponseEntity.ok(response);
	}
}
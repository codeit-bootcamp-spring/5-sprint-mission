package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보 생성
    @PostMapping
    public ResponseEntity<ReadStatusResponseDto> create(@RequestBody ReadStatusCreateRequest request) {
        ReadStatusResponseDto response = readStatusService.create(request);
        return ResponseEntity.ok(response);
    }

    // 특정 채널의 메시지 수신 정보 수정
    @PutMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusResponseDto> update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusResponseDto response = readStatusService.update(readStatusId, request);
        return ResponseEntity.ok(response);
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReadStatusResponseDto>> findAllByUser(@PathVariable UUID userId) {
        List<ReadStatusResponseDto> response = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(response);
    }

    // 특정 채널의 메시지 수신 정보 조회
    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<ReadStatusResponseDto>> findAllByChannel(@PathVariable UUID channelId) {
        List<ReadStatusResponseDto> response = readStatusService.findAllByChannelId(channelId);
        return ResponseEntity.ok(response);
    }
}

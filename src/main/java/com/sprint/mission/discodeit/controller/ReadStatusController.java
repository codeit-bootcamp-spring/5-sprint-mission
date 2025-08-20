package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.global.api.ApiResponse;
import com.sprint.mission.discodeit.global.error.ApiException;
import com.sprint.mission.discodeit.global.error.ErrorCode;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보 생성
    @PostMapping("/channels/{channelId}/receipts")
    public ResponseEntity<ApiResponse<ReadStatusCreateResponse>> createForChannel(
            @PathVariable UUID channelId,
            @Valid @RequestBody ReadStatusCreateRequest request) {

        if (request.channelId() != null && !request.channelId().equals(channelId)) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "경로 채널ID와 요청 채널ID가 다릅니다.");
        }

        ReadStatus created = readStatusService.create(request);
        ReadStatusCreateResponse response = new ReadStatusCreateResponse(
                request.userId(),
                request.channelId(),
                request.lastReadAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    // 특정 채널의 메시지 수신 정보 수정 (id 기반)
    @PutMapping("/receipts/{readStatusId}")
    public ResponseEntity<ApiResponse<ReadStatusUpdateResponse>> update(
            @PathVariable UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request) {
        ReadStatus updated = readStatusService.update(readStatusId, request);
        ReadStatusUpdateResponse response = new ReadStatusUpdateResponse(
                request.newLastReadAt()
        );
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @GetMapping("/users/{userId}/receipts")
    public ResponseEntity<ApiResponse<List<ReadStatus>>> listByUser(@PathVariable UUID userId) {
        List<ReadStatus> list = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}

package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    //메시지 수신 정보 생성
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<ReadStatusDto>> readStatusCreate(@RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatusDto readStatus = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(readStatus, "메시지 수신 정보를 생성했습니다"));
    }

    //메시지 수신 정보 수정
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<ReadStatusDto>> readStatusUpdate(@PathVariable("id") UUID id,
                                                                     @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatusDto readStatus = readStatusService.update(id, readStatusUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(readStatus, "메시지 수신정보 " + id + "를 수정했습니다"));
    }

    //메시지 수신 정보 조회
    @GetMapping
    public ResponseEntity<ApiResult<List<ReadStatusDto>>> readStatusFindByUserId(@RequestParam("userId") UUID userId) {
        List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(readStatuses));
    }
}

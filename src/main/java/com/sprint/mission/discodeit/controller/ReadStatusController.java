package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {

    @Qualifier("basicReadStatusService")
    private final ReadStatusService readStatusService;

    // 메시지 수신 정보 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponse> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        ReadStatusResponse response = readStatusService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 메시지 수신 정보 수정
    @RequestMapping(value = "/{statusId}", method = RequestMethod.PUT)
    public ResponseEntity<ReadStatusResponse> updateReadStatus(@PathVariable UUID statusId, @RequestBody ReadStatusUpdateRequest request) {
        if (request.getId() == null || !statusId.equals(request.getId())) {
            throw new IllegalArgumentException("ReadStatus ID in path does not match ID in request body.");
        }
        ReadStatusResponse response = readStatusService.update(request);
        return ResponseEntity.ok(response);
    }
}

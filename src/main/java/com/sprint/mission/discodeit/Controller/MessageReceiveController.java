package com.sprint.mission.discodeit.Controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/receive")
public class MessageReceiveController {

    private final ReadStatusService readStatusService;

    // 메시지 수신 정보 생성
    @RequestMapping(value = "/create", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatus created = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.ok(created);
    }

    // 메시지 수신 정보 수정
    @RequestMapping(value = "/update/{readStatusId}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReadStatus> update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
    ) {
        ReadStatus updated = readStatusService.update(readStatusId, readStatusUpdateRequest);
        return ResponseEntity.ok(updated);
    }

    // 메시지 수신 정보 조회
    @RequestMapping(value = "find/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> find(@PathVariable UUID userId) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(readStatuses);
    }



}

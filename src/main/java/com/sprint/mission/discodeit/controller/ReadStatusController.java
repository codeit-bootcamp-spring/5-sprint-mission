package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// 메시지 수신 정보 관리
// * [ ] 특정 채널의 메시지 수신 정보를 생성할 수 있다.
// * [ ] 특정 채널의 메시지 수신 정보를 수정할 수 있다.
// * [ ] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> createReadStatus(@RequestBody ReadStatusCreateRequest createRequest){
        ReadStatus readStatus = readStatusService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @RequestMapping(path = "update/{readStatusId}", method = RequestMethod.PUT)
    public ResponseEntity<ReadStatus> updateReadStatus(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest updateRequest
    ){
        ReadStatus updateReadStatus = readStatusService.update(readStatusId, updateRequest);
        return ResponseEntity.ok(updateReadStatus);

    }

    @RequestMapping(path = "{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findByUserId(@PathVariable("userId") UUID userId){
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(readStatuses);
    }


}

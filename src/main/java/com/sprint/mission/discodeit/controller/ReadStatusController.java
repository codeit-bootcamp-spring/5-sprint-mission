package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatus")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    //[ ] 특정 채널의 메시지 수신 정보를 생성할 수 있다.
    //채널에서 생성,삭제해서 없어도 될지도..
    @RequestMapping(path = "create", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> createReadStatus(
            @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.ok(readStatus);
    }

//            [ ] 특정 채널의 메시지 수신 정보를 수정할 수 있다.
    @RequestMapping(path = "update/{readStatusId}", method = RequestMethod.PUT)
    public ResponseEntity<ReadStatus> updateReadStatus(
            @RequestBody ReadStatusUpdateRequest request,
            @PathVariable UUID readStatusId
    ){
        ReadStatus readStatus = readStatusService.update(readStatusId, request);
        return ResponseEntity.ok(readStatus);
    }
//            [ ] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
    @RequestMapping(path = "find/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findReadStatusByUserId(@PathVariable UUID userId){
        List<ReadStatus> readStatusList = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(readStatusList);
    }


}

package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/readStatus")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/create", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<ReadStatus>> readStatusCreate(@RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatus readStatus = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(readStatus, "메시지 수신 정보를 생성했습니다"));
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<ReadStatus>> readStatusUpdate(@PathVariable("id") UUID id,
                                                                  @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus readStatus = readStatusService.update(id, readStatusUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(readStatus, "메시지 수신정보 " + id + "를 수정했습니다"));
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<List<ReadStatus>>> readStatusFindByUserId(@RequestParam("id") UUID id){
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(readStatuses));
    }
}

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(path = "create", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request){
        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @RequestMapping(path = "update", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatus> update(
            @RequestParam("readStatusId") UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ){
        ReadStatus updateReadStatus = readStatusService.update(readStatusId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updateReadStatus);

    }

    @RequestMapping(path = "findAllByUserId", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam("userId") UUID userId){
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
    }


}

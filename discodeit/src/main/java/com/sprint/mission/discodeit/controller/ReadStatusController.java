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


    @RequestMapping(path="create",method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> create(@RequestPart ReadStatusCreateRequest request){
        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.status(201).body(readStatus);
    }

    @RequestMapping(path="update",method=RequestMethod.POST)
    public ResponseEntity<ReadStatus> update(
            @RequestParam("readStatusId")UUID readStatusId,
            @RequestPart ReadStatusUpdateRequest request
    ){
        ReadStatus status=readStatusService.update(readStatusId,request);
        return ResponseEntity.status(200).body(status);
    }

    @RequestMapping(path="find",method=RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findByUserId(@RequestParam("userId")UUID userId){
        List<ReadStatus> status=readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(200).body(status);
    }



}

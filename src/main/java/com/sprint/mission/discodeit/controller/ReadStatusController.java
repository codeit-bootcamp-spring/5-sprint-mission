package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/readStatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(path="/create", method= RequestMethod.POST)
    public ResponseEntity<ReadStatus> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.status(200).body(readStatus);
    }

    @RequestMapping(path="/{id}/update", method=RequestMethod.POST)
    public ResponseEntity<ReadStatus> updateReadStatus(@PathVariable UUID id, @RequestBody ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusService.update(id, request);
        return ResponseEntity.status(201).body(readStatus);
    }

    @RequestMapping(path="/{id}/findAll", method=RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllReadStatusByUserId(@PathVariable UUID id) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(id);
        return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
    }
}

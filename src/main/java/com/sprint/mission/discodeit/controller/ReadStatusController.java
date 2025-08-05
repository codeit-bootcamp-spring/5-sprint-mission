package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.status.read.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.status.read.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.status.read.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/read-status")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(method = POST)
    public ResponseEntity<ReadStatusResponse> create(@RequestBody CreateReadStatusRequest request) {
        ReadStatusResponse created = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(value = "/{userId}", method = GET)
    public ResponseEntity<List<ReadStatusResponse>> getReadStatus(@PathVariable UUID userId) {
        List<ReadStatusResponse> readStatusList = readStatusService.getAllByUserId(userId);
        return ResponseEntity.ok(readStatusList);
    }

    @RequestMapping(method = PUT)
    public ResponseEntity<ReadStatusResponse> update(@RequestBody UpdateReadStatusRequest request) {
        ReadStatusResponse updated = readStatusService.update(request);
        if (updated == null) throw new NotFoundException("Read Status not found");
        return ResponseEntity.ok(updated);
    }
}

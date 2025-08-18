package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(path="create")
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request){
        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @RequestMapping(path="update")
    public ResponseEntity<ReadStatus>update(@RequestParam("readStatusId") UUID readStatusId,
                                            @RequestBody ReadStatusUpdateRequest request){
        ReadStatus readStatus = readStatusService.update(readStatusId, request);
        return ResponseEntity.status(HttpStatus.OK).body(readStatus);
    }

    @RequestMapping(path="findAllByUserId")
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam("userId") UUID userId){
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
    }


}

package com.sprint.mission.discodeit.controller.restController;

import com.sprint.mission.discodeit.dto.request.AddReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/read-status")
public class ReadStatusController {

    private final ReadStatusService readStatusService;


    @RequestMapping(path="", method = RequestMethod.POST)
    public ReadStatus addReadStatus(
            @RequestBody AddReadStatusRequest addReadStatusRequest
    ){
        return readStatusService.addReadStatus(addReadStatusRequest);
    }

    @RequestMapping(path="/{readStatusId}", method = RequestMethod.POST)
    public ReadStatus updateReadStatus(
            @PathVariable UUID readStatusId
    ){
        return readStatusService.updateReadStatus(readStatusId);
    }

    @RequestMapping(path="/{userId}", method = RequestMethod.GET)
    public List<ReadStatus> getAllReadStatusByUserId(
            @PathVariable UUID userId
    ){
        return readStatusService.getAllReadStatusByUserId(userId);
    }
}

package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@ControllerAdvice
@Controller
@RequestMapping("/readStatus")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        ReadStatus readStatus = readStatusService.create(request);
        return "생성 성공\n" + readStatus.toString();
    }

    @ResponseBody
    @RequestMapping(value = {"/update"}, method = RequestMethod.POST)
    public String update(@RequestBody ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusService.update(request);
        return "업데이트 성공\n" + readStatus.toString();
    }

    @ResponseBody
    @RequestMapping(value = {"/find/{userId}", "findAllByUserId/{userId}"}, method = RequestMethod.GET)
    public String findByUser(@PathVariable UUID userId) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
        return readStatuses.toString().replace("), ", ")\n");
    }
}

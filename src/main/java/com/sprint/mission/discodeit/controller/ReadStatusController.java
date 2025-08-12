package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReadStatusController {

    private final ChannelService channelService;
    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/readStatus", method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto.DetailResponse> createReadStatus(@RequestBody ReadStatusDto.CreateRequest request) {
        return ResponseEntity.ok(readStatusService.create(request));
    }

    @RequestMapping(value = "/readStatus/channel/{channelId}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateReadStatus(@PathVariable UUID channelId) {
        // TODO 나중에 로그인 중인 사용자만 처리하면 될듯?

        channelService.findById(channelId)
            .getUserIds()
            .forEach(userId -> {
                readStatusService.findAllByUserId(userId)
                    .forEach(readStatus ->
                    {
                        readStatusService.update(readStatus.getId());
                    });
            });
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/readStatus/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto.DetailResponse>> getReadStatusByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}

package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping
    public SseEmitter connect(
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId,
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ) {
        UUID receiverId = userDetails.getUserResponse().getId();
        UUID lastEventUUID = lastEventId != null ? UUID.fromString(lastEventId) : null;

        log.debug("[SseController] SSE 연결 요청 - receiverId: {}, lastEventId: {}",
                receiverId, lastEventUUID);

        return sseService.connect(receiverId, lastEventUUID);
    }
}

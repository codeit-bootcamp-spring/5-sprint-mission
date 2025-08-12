package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ChannelUnreadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.response.ReadStatusResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message/status")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    /** [웹 API 요구사항] 메시지 수신 정보 관리
     * - 특정 채널의 메시지 수신 정보를 생성할 수 있다.
     * - 최초 생성 시점에는 새 객체를 만들고 저장
     * - 요청 시 읽은 시각(readAt)도 함께 받을 수 있음
     * -기존 데이터가 있으면 수정, 없으면 생성
     * */
    @RequestMapping(method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<ReadStatusResponse> saveOrUpdate(@Valid @ModelAttribute ReadStatusDto dto) {
        ReadStatusResponse response = readStatusService.updateLastReadAt(dto.userId(), dto.channelId(), dto.messageId());
        return ResponseEntity.ok(response);
    }

    /** [웹 API 요구사항] 메시지 수신 정보 관리
     * - 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
     * - 해당 사용자가 속한 모든 채널의 읽음/안읽음 상태 반환
     * */
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelUnreadStatusDto>> getReadStatusByUser(@PathVariable UUID userId) {
        List<ChannelUnreadStatusDto> unreadList = readStatusService.getUnreadChannels(userId);
        return ResponseEntity.ok(unreadList);
    }

    /**
     * 특정 채널에서 해당 사용자의 읽지 않은 메시지 개수 조회
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Integer>> countUnreadMessages(
            @RequestParam UUID userId,
            @RequestParam UUID channelId
    ) {
        int count = readStatusService.countUnreadMessages(userId, channelId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }
}

package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
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
@RequestMapping("/read/status")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    /** [웹 API 요구사항] 메시지 수신 정보 관리
     * ex) 사용자가 채널에 들어왔을 때
     * - 1. 최초 방문 시 메시지가 존재한다면 마지막 메시지가 생성된 시간, 없으면 null
     * - 2. 두번째 방문 부터(기존 데이터가 있으면) 새로운 시간으로 업데이트
     * */
    @RequestMapping(method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<ReadStatusDto.response> saveOrUpdate(@Valid @ModelAttribute ReadStatusDto.create dto) {
        ReadStatusDto.response response = readStatusService.updateLastReadAt(dto);
        return ResponseEntity.ok(response);
    }

    /** [웹 API 요구사항] 메시지 수신 정보 관리
     * - 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
     * - 해당 사용자가 속한 모든 채널의 읽음/안읽음 상태 반환
     * */
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto.unread>> getReadStatusByUser(@PathVariable UUID userId) {
        List<ReadStatusDto.unread> unreadList = readStatusService.getUnreadChannels(userId);
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

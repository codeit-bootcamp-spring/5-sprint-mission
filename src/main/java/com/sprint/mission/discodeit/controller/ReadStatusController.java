package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/readStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    /** [웹 API 요구사항] 메시지 수신 정보 관리
     * ex) 사용자가 채널에 들어왔을 때
     * - 1. 최초 방문 시 메시지가 존재한다면 마지막 메시지가 생성된 시간, 없으면 null
     * - 2. 두번째 방문 부터(기존 데이터가 있으면) 새로운 시간으로 업데이트
     * */
    @PostMapping
    public ResponseEntity<ReadStatusResponse> saveOrUpdate(@Valid @RequestBody ReadStatusRequest.Create req) {
        Instant lastReadAt = Instant.now();
        ReadStatus readStatus = readStatusService.updateLastReadAt(req.userId(), req.channelId(), lastReadAt);
        return ResponseEntity.ok(ReadStatusResponse.of(readStatus));
    }

    /** [웹 API 요구사항] 메시지 수신 정보 관리
     * - 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
     * - 해당 사용자가 속한 모든 채널의 읽음/안읽음 상태 반환
     * */
    @GetMapping(params = "userId")
    public ResponseEntity<List<ReadStatusDto.unread>> getReadStatusByUser(@RequestParam UUID userId) {
        List<ReadStatusDto.unread> unreadList = readStatusService.getUnreadChannels(userId);
        return ResponseEntity.ok(unreadList);
    }

    /**
     * 특정 채널에서 해당 사용자의 읽지 않은 메시지 개수 조회
     */
    @RequestMapping(value = "count", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Integer>> countUnreadMessages(
            @RequestParam UUID userId,
            @RequestParam UUID channelId
    ) {
        int count = readStatusService.countUnreadMessages(userId, channelId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }
}

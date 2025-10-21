package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor // final 필드를 매개변수로 받는 생성자를 롬복이 자동 생성 (생성자 주입)
@RestController // REST API용 컨트롤러, @Controller + @ResponseBody 합친 것
@RequestMapping("/api/channels") // 이 컨트롤러의 모든 엔드포인트는 /api/channels 하위 경로로 매핑
public class ChannelController implements ChannelApi { // ChannelApi 인터페이스 구현

    private final ChannelService channelService; // 비즈니스 로직을 담당하는 서비스 계층 의존성 주입

    @PostMapping(path = "public") // POST /api/channels/public
    public ResponseEntity<ChannelDto> create(@RequestBody @Valid PublicChannelCreateRequest request) {
        log.info("공개 채널 생성 요청: {}", request);
        // 요청 바디(JSON)를 PublicChannelCreateRequest DTO로 매핑
        ChannelDto createdChannel = channelService.create(request); // 서비스 호출 → 채널 생성
        log.debug("공개 채널 생성 응답: {}", createdChannel);
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 Created 상태 코드
                .body(createdChannel); // 생성된 채널 DTO 반환
    }

    @PostMapping(path = "private") // POST /api/channels/private
    public ResponseEntity<ChannelDto> create(@RequestBody @Valid PrivateChannelCreateRequest request) {
        log.info("비공개 채널 생성 요청: {}", request);
        // 요청 바디(JSON)를 PrivateChannelCreateRequest DTO로 매핑
        ChannelDto createdChannel = channelService.create(request); // 서비스 호출 → 채널 생성
        log.debug("비공개 채널 생성 응답: {}", createdChannel);
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 Created
                .body(createdChannel); // 생성된 채널 DTO 반환
    }

    @PatchMapping(path = "{channelId}") // PATCH /api/channels/{channelId}
    public ResponseEntity<ChannelDto> update(
            @PathVariable("channelId") UUID channelId,
            @RequestBody PublicChannelUpdateRequest request) {
        // URL 경로 변수 channelId 추출, 요청 바디 매핑
        log.info("채널 수정 요청: id={}, request={}", channelId, request);
        ChannelDto updatedChannel = channelService.update(channelId, request); // 서비스 호출 → 채널 수정
        log.debug("채널 수정 응답: {}", updatedChannel);
        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(updatedChannel); // 수정된 채널 DTO 반환
    }

    @DeleteMapping(path = "{channelId}") // DELETE /api/channels/{channelId}
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
        log.info("채널 삭제 요청: id={}", channelId);
        channelService.delete(channelId); // 서비스 호출 → 채널 삭제
        log.debug("채널 삭제 완료");
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT) // 204 No Content
                .build(); // 바디 없음
    }

    @GetMapping // GET /api/channels?userId={userId}
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
        log.info("사용자별 채널 목록 조회 요청: userId={}", userId);
        // 쿼리 파라미터 userId 추출
        List<ChannelDto> channels = channelService.findAllByUserId(userId); // 서비스 호출 → 해당 유저가 속한 채널 목록 조회
        log.debug("사용자별 채널 목록 조회 응답: count={}", channels.size());
        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(channels); // 채널 리스트 DTO 반환
    }
}


package com.sprint.mission.discodeit.controller; // 컨트롤러 패키지 선언

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor                                         // 생성자 주입(Lombok)
@RestController                                                  // REST 컨트롤러 선언
@RequestMapping("/api/channels")                                 // 기본 URL 매핑
public class ChannelController implements ChannelApi {                                 // 클래스 시작 (메서드명/라우트는 기존과 동일 유지)

    private final ChannelService channelService;                 // 채널 비즈니스 로직 의존성
    private final ChannelMapper channelMapper;                   // 엔티티→DTO 변환 매퍼 의존성(2개 인자 요구)

    @PostMapping(path = "public")                                // 공개 채널 생성 엔드포인트(기존 메서드명: create)
    public ResponseEntity<ChannelDto> create(                    // 반환 타입을 ChannelDto로 리팩토링
                                                                 @RequestBody PublicChannelCreateRequest request      // 요청 본문 바인딩
    ) {
        Channel createdChannel = channelService.create(request); // 서비스 호출로 엔티티 생성/저장
        ChannelDto tmp = channelService.find(createdChannel.getId()); // 서비스의 find로 DTO 재조회(참가자 IDs 포함)
        ChannelDto body = channelMapper.toDto(                   // 최종 응답 DTO 생성
                createdChannel,                                  // 1) 엔티티
                tmp.participantIds()                             // 2) 참가자 ID 목록(서비스 DTO에서 추출)
        );
        return ResponseEntity                                    // 응답 빌더 시작
                .status(HttpStatus.CREATED)                      // 201 Created
                .body(body);                                     // DTO 본문 반환
    }

    @PostMapping(path = "private")                               // 비공개 채널 생성 엔드포인트(기존 메서드명: create)
    public ResponseEntity<ChannelDto> create(                    // 오버로드된 create 유지, 반환은 ChannelDto
                                                                 @RequestBody PrivateChannelCreateRequest request     // 요청 본문 바인딩
    ) {
        Channel createdChannel = channelService.create(request); // 서비스 호출로 엔티티 생성/저장
        ChannelDto tmp = channelService.find(createdChannel.getId()); // 서비스의 find로 DTO 재조회
        ChannelDto body = channelMapper.toDto(                   // 최종 응답 DTO 생성
                createdChannel,                                  // 1) 엔티티
                tmp.participantIds()                             // 2) 참가자 ID 목록
        );
        return ResponseEntity                                    // 응답 빌더 시작
                .status(HttpStatus.CREATED)                      // 201 Created
                .body(body);                                     // DTO 본문 반환
    }

    @PatchMapping(path = "{channelId}")                          // 채널 정보 수정 엔드포인트(기존 메서드명: update)
    public ResponseEntity<ChannelDto> update(                    // 반환 타입을 ChannelDto로 리팩토링
                                                                 @PathVariable("channelId") UUID channelId,           // 경로 변수: 채널 ID
                                                                 @RequestBody PublicChannelUpdateRequest request      // 요청 본문 바인딩
    ) {
        Channel updatedChannel = channelService.update(channelId, request); // 서비스 호출로 엔티티 수정
        ChannelDto tmp = channelService.find(updatedChannel.getId());       // 서비스의 find로 DTO 재조회
        ChannelDto body = channelMapper.toDto(                   // 최종 응답 DTO 생성
                updatedChannel,                                  // 1) 엔티티
                tmp.participantIds()                             // 2) 참가자 ID 목록
        );
        return ResponseEntity                                    // 응답 빌더 시작
                .status(HttpStatus.OK)                           // 200 OK
                .body(body);                                     // DTO 본문 반환
    }

    @DeleteMapping(path = "{channelId}")                         // 채널 삭제 엔드포인트(기존 메서드명: delete)
    public ResponseEntity<Void> delete(                          // 본문 없는 응답
                                                                 @PathVariable("channelId") UUID channelId            // 경로 변수: 채널 ID
    ) {
        channelService.delete(channelId);                        // 서비스 호출로 삭제 처리
        return ResponseEntity                                    // 응답 빌더 시작
                .status(HttpStatus.NO_CONTENT)                   // 204 No Content
                .build();                                        // 본문 없이 반환
    }

    @GetMapping                                                 // 채널 목록 조회 엔드포인트(기존 메서드명: findAll)
    public ResponseEntity<List<ChannelDto>> findAll(            // 반환 타입 동일: List<ChannelDto>
                                                                @RequestParam("userId") UUID userId                 // 쿼리 파라미터: 사용자 ID
    ) {
        List<ChannelDto> channels =                             // 서비스가 DTO 리스트 반환
                channelService.findAllByUserId(userId);         // 사용자 기준 채널 조회
        return ResponseEntity                                   // 응답 빌더 시작
                .status(HttpStatus.OK)                          // 200 OK
                .body(channels);                                // DTO 목록 본문 반환
    }
}

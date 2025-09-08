package com.sprint.mission.discodeit.controller; // 컨트롤러 패키지 선언

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor                                         // 생성자 주입(Lombok)
@RestController                                                  // REST 컨트롤러 선언
@RequestMapping("/api/readStatuses")                             // 기본 URL 매핑
public class ReadStatusController implements ReadStatusApi {                              // 클래스 시작(메서드/라우트는 기존과 동일 유지)

    private final ReadStatusService readStatusService;           // 읽음 상태 서비스 의존성
    private final ReadStatusMapper readStatusMapper;             // 엔티티→DTO 매퍼 의존성

    @PostMapping                                                 // 생성 엔드포인트(기존 메서드명: create)
    public ResponseEntity<ReadStatusDto> create(                 // 반환 타입을 ReadStatusDto로 리팩토링
                                                                 @RequestBody ReadStatusCreateRequest request         // 요청 본문을 DTO로 바인딩
    ) {
        ReadStatus created = readStatusService.create(request);  // 서비스에 생성 위임(엔티티 반환)
        ReadStatusDto body = readStatusMapper.toDto(created);    // 엔티티를 DTO로 변환
        return ResponseEntity                                    // 응답 빌더 시작
                .status(HttpStatus.CREATED)                      // 201 Created
                .body(body);                                     // 생성된 DTO 반환
    }

    @PatchMapping(path = "/{readStatusId}")                      // 수정 엔드포인트(기존 메서드명: update)
    public ResponseEntity<ReadStatusDto> update(                 // 반환 타입을 ReadStatusDto로 리팩토링
                                                                 @PathVariable("readStatusId") UUID readStatusId,     // PathVariable로 대상 지정
                                                                 @RequestBody ReadStatusUpdateRequest request         // 수정 값 바인딩
    ) {
        ReadStatus updated = readStatusService.update(           // 서비스에 수정 위임
                readStatusId, request                            // 식별자/요청 전달
        );                                                       // 엔티티 반환
        ReadStatusDto body = readStatusMapper.toDto(updated);    // 엔티티→DTO 변환
        return ResponseEntity                                    // 응답 빌더 시작
                .status(HttpStatus.OK)                           // 200 OK
                .body(body);                                     // 수정 결과 DTO 반환
    }

    @GetMapping                                                 // 사용자별 목록 조회 엔드포인트(기존 메서드명: findAllByUserId)
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId( // 반환 타입을 List<ReadStatusDto>로 리팩토링
                                                                @RequestParam(name = "userId", required = true) UUID userId // ★ 필수 쿼리 파라미터
    ) {
        List<ReadStatus> entities =                              // 서비스에 조회 위임(엔티티 목록)
                readStatusService.findAllByUserId(userId);       // 사용자 기준 조회
        List<ReadStatusDto> body =                               // 엔티티 목록→DTO 목록 변환
                entities.stream()                                // 스트림 시작
                        .map(readStatusMapper::toDto)            // 개별 매핑
                        .toList();                                // 리스트 수집(JDK 16+; 낮으면 Collectors.toList())
        return ResponseEntity                                    // 응답 빌더 시작
                .status(HttpStatus.OK)                           // 200 OK
                .body(body);                                     // DTO 리스트 반환
    }
}

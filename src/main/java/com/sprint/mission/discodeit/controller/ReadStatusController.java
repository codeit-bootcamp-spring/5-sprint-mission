package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor // final 필드를 매개변수로 받는 생성자 자동 생성 (생성자 주입)
@RestController // REST API 컨트롤러
@RequestMapping("/api/readStatuses") // 모든 엔드포인트는 /api/readStatuses 하위 경로
public class ReadStatusController implements ReadStatusApi { // ReadStatusApi 인터페이스 구현

    private final ReadStatusService readStatusService; // 읽음 상태 관련 비즈니스 로직을 처리하는 서비스

    @PostMapping // POST /api/readStatuses
    public ResponseEntity<ReadStatusDto> create(@RequestBody ReadStatusCreateRequest request) {
        // 요청 바디(JSON)를 ReadStatusCreateRequest DTO로 매핑
        ReadStatusDto createdReadStatus = readStatusService.create(request); // 서비스 호출 → 읽음 상태 생성
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 Created
                .body(createdReadStatus);   // 생성된 읽음 상태 DTO 반환
    }

    @PatchMapping(path = "{readStatusId}") // PATCH /api/readStatuses/{readStatusId}
    public ResponseEntity<ReadStatusDto> update(@PathVariable("readStatusId") UUID readStatusId,
                                                @RequestBody ReadStatusUpdateRequest request) {
        // URL 경로 변수 readStatusId 추출 + 요청 바디(JSON) 매핑
        ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request); // 서비스 호출 → 읽음 상태 수정
        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(updatedReadStatus); // 수정된 읽음 상태 DTO 반환
    }

    @GetMapping // GET /api/readStatuses?userId={userId}
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
        // 쿼리 파라미터 userId 추출
        List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId); // 서비스 호출 → 해당 유저의 읽음 상태 전체 조회
        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(readStatuses);   // 읽음 상태 DTO 리스트 반환
    }
}


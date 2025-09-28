package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor                                        // 생성자 주입(Lombok)
@RestController                                                 // REST 컨트롤러 선언
@RequestMapping("/api/binaryContents")                          // 기본 URL 매핑
public class BinaryContentController implements BinaryContentApi { // 클래스 시작(메서드/라우트는 기존과 동일 유지)

    private final BinaryContentService binaryContentService;    // 바이너리 콘텐츠 서비스 의존성
    private final BinaryContentStorage binaryContentStorage;    // ★ 추가: 실제 파일 저장/다운로드 스토리지 의존성

    @GetMapping(path = "{binaryContentId}")                     // 단건 조회 엔드포인트(기존 메서드명: find)
    public ResponseEntity<BinaryContentDto> find(
            @PathVariable("binaryContentId") UUID binaryContentId) { // 경로 변수: 파일 ID
        BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);// 서비스에서 엔티티 단건 조회
        return ResponseEntity                                   // 응답 빌더 시작
                .status(HttpStatus.OK)                          // 200 OK
                .body(binaryContent);                                    // DTO 본문 반환
    }

    @GetMapping                                                // 다건 조회 엔드포인트(기존 메서드명: findAllByIdIn)
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) { // 쿼리 파라미터: 파일 ID 리스트
        List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds); // ID 집합으로 검색
        return ResponseEntity                                   // 응답 빌더 시작
                .status(HttpStatus.OK)                          // 200 OK
                .body(binaryContents);                                    // DTO 리스트 본문 반환
    }

    @GetMapping(path = "{binaryContentId}/download")
    public ResponseEntity<?> download(// 반환은 스토리지 구현에 맞춰 와일드카드
        @PathVariable("binaryContentId") UUID binaryContentId) { // 경로 변수: 파일 ID
        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId); // 메타데이터 조회
        return binaryContentStorage.download(binaryContentDto);                       // 스토리지에 다운로드 응답 위임
    }
}

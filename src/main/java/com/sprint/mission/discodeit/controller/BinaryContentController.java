package com.sprint.mission.discodeit.controller; // 컨트롤러 패키지 선언

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;          // ★ 추가: 스토리지 주입을 위한 import
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
    private final BinaryContentMapper binaryContentMapper;      // 엔티티→DTO 매퍼 의존성
    private final BinaryContentStorage binaryContentStorage;    // ★ 추가: 실제 파일 저장/다운로드 스토리지 의존성

    @GetMapping(path = "{binaryContentId}")                     // 단건 조회 엔드포인트(기존 메서드명: find)
    public ResponseEntity<BinaryContentDto> find(               // 반환 타입을 BinaryContentDto로 리팩토링
                                                                @PathVariable("binaryContentId") UUID binaryContentId // 경로 변수: 파일 ID
    ) {
        BinaryContent binaryContent =                           // 서비스에서 엔티티 단건 조회
                binaryContentService.find(binaryContentId);     // 존재하지 않으면 예외 가정
        BinaryContentDto body =                                 // 엔티티→DTO 변환
                binaryContentMapper.toDto(binaryContent);       // 필요 시 toDtoWithoutBytes로 교체 가능
        return ResponseEntity                                   // 응답 빌더 시작
                .status(HttpStatus.OK)                          // 200 OK
                .body(body);                                    // DTO 본문 반환
    }

    @GetMapping                                                // 다건 조회 엔드포인트(기존 메서드명: findAllByIdIn)
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn( // 반환 타입을 List<BinaryContentDto>로 리팩토링
                                                                 @RequestParam("binaryContentIds") List<UUID> binaryContentIds // 쿼리 파라미터: 파일 ID 리스트
    ) {
        List<BinaryContent> binaryContents =                    // 서비스에서 엔티티 목록 조회
                binaryContentService.findAllByIdIn(binaryContentIds); // ID 집합으로 검색
        List<BinaryContentDto> body =                           // 엔티티 목록→DTO 목록 변환
                binaryContents.stream()                         // 스트림 시작
                        .map(binaryContentMapper::toDto)        // 개별 매핑(toDtoWithoutBytes 대체 가능)
                        .toList();                              // 리스트 수집(JDK 16+; 낮으면 Collectors.toList())
        return ResponseEntity                                   // 응답 빌더 시작
                .status(HttpStatus.OK)                          // 200 OK
                .body(body);                                    // DTO 리스트 본문 반환
    }

    @GetMapping(path = "{binaryContentId}/download")            // ★ 추가: 파일 다운로드 엔드포인트
    public ResponseEntity<?> download(                          // 반환은 스토리지 구현에 맞춰 와일드카드
                                                                @PathVariable("binaryContentId") UUID binaryContentId // 경로 변수: 파일 ID
    ) {
        BinaryContent meta = binaryContentService.find(binaryContentId); // 메타데이터 조회
        BinaryContentDto dto = binaryContentMapper.toDto(meta);          // DTO로 변환(바이트는 스토리지에서 로드)
        return binaryContentStorage.download(dto);                       // 스토리지에 다운로드 응답 위임
    }
}

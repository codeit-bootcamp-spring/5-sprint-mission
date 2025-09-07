package com.sprint.mission.discodeit.controller; // 컨트롤러 패키지 선언

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor                                             // 의존성 생성자 자동 생성
@RestController                                                      // REST 컨트롤러 선언
@RequestMapping("/api/messages")                                     // 기본 경로 매핑
public class MessageController implements MessageApi {                                     // 클래스 시작 (MessageApi 제거)

    private final MessageService messageService;                     // 메시지 비즈니스 로직 의존성
    private final MessageMapper messageMapper;                       // 엔티티→DTO 변환 매퍼 의존성
    private final PageResponseMapper pageResponseMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)     // 멀티파트 요청(JSON + 파일 리스트) 소비
    public ResponseEntity<MessageDto> create(                        // 반환 타입을 MessageDto로 변경
                                                                     @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest, // JSON 파트 바인딩
                                                                     @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments // 파일 리스트(선택)
    ) {
        List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(
                        attachments)                                         // 첨부파일 리스트 null 안전 처리
                .map(files -> files.stream()                             // 파일 스트림 생성
                        .map(file -> {                                       // 각 파일을
                            try {                                            // 바이트 추출 중 예외 처리
                                return new BinaryContentCreateRequest(        // 바이너리 DTO로 변환
                                        file.getOriginalFilename(),               // 원본 파일명
                                        file.getContentType(),                    // MIME 타입
                                        file.getBytes()                           // 파일 바이트
                                );                                            // DTO 생성 종료
                            } catch (IOException e) {                         // IO 예외 발생 시
                                throw new RuntimeException(e);                // 런타임 예외로 래핑
                            }                                                 // catch 종료
                        })                                                    // map 종료
                        .toList())                                            // 스트림을 리스트로 수집
                .orElse(new ArrayList<>());                               // 첨부가 없으면 빈 리스트 사용

        Message createdMessage = messageService
                .create(messageCreateRequest, attachmentRequests);        // 서비스 호출로 메시지 생성

        MessageDto body = messageMapper.toDto(createdMessage);        // 생성된 엔티티를 DTO로 변환
        return ResponseEntity                                        // 응답 빌더 시작
                .status(HttpStatus.CREATED)                              // 201 Created 상태코드
                .body(body);                                             // DTO 본문 반환
    }

    @PatchMapping(path = "/{messageId}")                             // 기존 update 라우트 유지
    public ResponseEntity<MessageDto> update(                        // 반환 타입을 MessageDto로 변경
                                                                     @PathVariable("messageId") UUID messageId,                   // 경로 변수에서 메시지 ID 추출
                                                                     @RequestBody MessageUpdateRequest request                    // 요청 본문(JSON) 바인딩
    ) {
        Message updatedMessage = messageService
                .update(messageId, request);                             // 서비스 호출로 메시지 수정
        return ResponseEntity                                        // 응답 빌더 시작
                .status(HttpStatus.OK)                                   // 200 OK 상태코드
                .body(messageMapper.toDto(updatedMessage));              // DTO 본문 반환
    }

    @DeleteMapping(path = "/{messageId}")                            // 식별자는 PathVariable로 적용
    public ResponseEntity<Void> delete(                              // 메시지 삭제 엔드포인트
                                                                     @PathVariable("messageId") UUID messageId                    // 경로 변수에서 메시지 ID 추출
    ) {
        messageService.delete(messageId);                            // 서비스 호출로 메시지 삭제
        return ResponseEntity                                        // 응답 빌더 시작
                .status(HttpStatus.NO_CONTENT)                           // 204 No Content 상태코드
                .build();                                                // 본문 없이 응답
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)         // 응답은 JSON
    public ResponseEntity<List<MessageDto>> findAllByChannelId(      // 반환 타입을 List<MessageDto>로 변경
                                                                     @RequestParam(name = "channelId", required = true) UUID channelId // ★ 필수 쿼리 파라미터
    ) {
        List<Message> messages = messageService
                .findAllByChannelId(channelId);                          // 서비스 호출로 목록 조회
        List<MessageDto> body = messages.stream()                    // 엔티티 목록 스트림
                .map(messageMapper::toDto)                               // 각 엔티티를 DTO로 매핑
                .toList();                                               // 리스트로 수집(JDK 16+)
        return ResponseEntity                                        // 응답 빌더 시작
                .status(HttpStatus.OK)                                   // 200 OK 상태코드
                .body(body);                                             // DTO 목록 본문 반환
    }

    @GetMapping(path = "/slice", produces = "application/json")
    public ResponseEntity<PageResponse<MessageDto>> findSliceByChannelId(
            @RequestParam("channelId") UUID channelId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size
    ) {
        // 강제 정렬: 최근 메시지 순
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        var slice = messageService.findSliceByChannelId(channelId, pageable);

        // 컨트롤러에서 DTO 매핑 유지
        var body = pageResponseMapper.fromSlice(slice, messageMapper::toDto /* or ::toDtoWithoutBytes */);

        return ResponseEntity.ok(body);
    }
}

package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor // Lombok 어노테이션: final 필드를 자동으로 생성자 주입해줌
@RestController // REST API 컨트롤러임을 나타냄
@RequestMapping("/api/messages") // 모든 메서드가 "/api/messages" 경로를 기반으로 실행됨
public class MessageController implements MessageApi {

    // 메시지 관련 비즈니스 로직을 처리하는 서비스
    private final MessageService messageService;

    // 메시지 생성 API (파일 업로드 포함, multipart/form-data 형식으로 받음)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(
            // 메시지 본문 정보를 담은 요청 DTO
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            // 첨부파일 리스트 (선택적으로 받을 수 있음)
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        // 첨부파일이 존재한다면 BinaryContentCreateRequest 리스트로 변환, 없으면 빈 리스트 생성
        List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
                .map(files -> files.stream()
                        .map(file -> {
                            try {
                                // 파일 정보를 DTO로 변환 (이름, 타입, 바이트 데이터)
                                return new BinaryContentCreateRequest(
                                        file.getOriginalFilename(),
                                        file.getContentType(),
                                        file.getBytes()
                                );
                            } catch (IOException e) {
                                // 변환 과정에서 IOException 발생 시 RuntimeException으로 래핑
                                throw new RuntimeException(e);
                            }
                        })
                        .toList())
                .orElse(new ArrayList<>());

        // 서비스 계층을 통해 메시지 생성
        MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);

        // 생성된 메시지를 201 CREATED 상태 코드와 함께 응답
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMessage);
    }

    // 메시지 수정 API (특정 메시지 ID 기반)
    @PatchMapping(path = "{messageId}")
    public ResponseEntity<MessageDto> update(@PathVariable("messageId") UUID messageId,
                                             @RequestBody MessageUpdateRequest request) {
        // 서비스 계층에서 메시지 수정 처리
        MessageDto updatedMessage = messageService.update(messageId, request);

        // 수정된 메시지를 200 OK 상태 코드와 함께 응답
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedMessage);
    }

    // 메시지 삭제 API (특정 메시지 ID 기반)
    @DeleteMapping(path = "{messageId}")
    public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
        // 서비스 계층에서 메시지 삭제 처리
        messageService.delete(messageId);

        // 204 No Content 상태 코드로 응답 (본문 없음)
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    // 특정 채널의 메시지 목록 조회 API (커서 기반 페이지네이션 포함)
    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
            // 조회할 채널의 ID (필수 값)
            @RequestParam("channelId") UUID channelId,
            // 커서 기반 페이지네이션에 사용될 시점 (선택 값)
            @RequestParam(value = "cursor", required = false) Instant cursor,
            // 페이징 기본값 설정: 1페이지 크기 50, createdAt 기준 내림차순 정렬
            @PageableDefault(
                    size = 50,
                    page = 0,
                    sort = "createdAt",
                    direction = Direction.DESC
            ) Pageable pageable) {
        // 서비스 계층에서 메시지 조회
        PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor,
                pageable);

        // 조회된 메시지 페이지를 200 OK 상태 코드와 함께 응답
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messages);
    }
}


package com.sprint.mission.discodeit.controller; // 컨트롤러 패키지 선언

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest; // 첨부파일 바이너리 DTO 임포트
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest; // 메시지 생성 요청 DTO 임포트
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest; // 메시지 수정 요청 DTO 임포트
import com.sprint.mission.discodeit.entity.Message; // 메시지 엔티티 임포트
import com.sprint.mission.discodeit.service.MessageService; // 메시지 서비스 임포트
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor; // Lombok RequiredArgsConstructor 임포트
import org.springframework.http.HttpStatus; // HTTP 상태코드 상수 임포트
import org.springframework.http.MediaType; // 미디어 타입 상수 임포트
import org.springframework.http.ResponseEntity; // 응답 래퍼 클래스 임포트
import org.springframework.web.bind.annotation.*; // REST 애너테이션 임포트
import org.springframework.web.multipart.MultipartFile; // 멀티파트 파일 타입 임포트

import java.io.IOException; // IO 예외 임포트
import java.util.ArrayList; // ArrayList 임포트
import java.util.List; // List 임포트
import java.util.Optional; // Optional 임포트
import java.util.UUID; // UUID 임포트

// ===== Swagger/OpenAPI 애너테이션 임포트(간단 버전) =====
import io.swagger.v3.oas.annotations.Operation; // 엔드포인트 요약/설명
import io.swagger.v3.oas.annotations.media.Content; // 응답 Content
import io.swagger.v3.oas.annotations.media.Schema; // 응답 스키마
import io.swagger.v3.oas.annotations.responses.ApiResponse; // 단일 응답
import io.swagger.v3.oas.annotations.responses.ApiResponses; // 복수 응답
import io.swagger.v3.oas.annotations.tags.Tag; // 태그 그룹
// =====================================================

@RequiredArgsConstructor // final 필드에 대한 생성자를 자동 생성
@RestController // @Controller + @ResponseBody 조합을 대체하는 REST 전용 컨트롤러
@RequestMapping("/api/messages") // 베이스 경로는 기존 그대로 유지
@Tag( // Swagger UI에서 'Messages' 그룹으로 묶기 위한 태그
    name = "Messages", // 그룹 이름
    description = "메시지 생성·수정·삭제·조회 API" // 그룹 설명
)
public class MessageController { // 컨트롤러 클래스 시작

    private final MessageService messageService; // 메시지 비즈니스 로직을 처리하는 서비스 의존성

    @PostMapping( // 생성은 POST 메서드로 명시
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE, // 멀티파트 요청(JSON + 파일 리스트) 소비
        produces = MediaType.APPLICATION_JSON_VALUE // 응답은 JSON
    )
    @Operation( // Swagger: 엔드포인트 요약/설명
        summary = "메시지 생성", // 요약
        description = "본문(JSON)과 첨부파일(옵션, 멀티파트)을 함께 받아 메시지를 생성합니다." // 설명
        , operationId = "createMessage"
    )
    @ApiResponses({ // Swagger: 가능한 응답 정의
        @ApiResponse(
            responseCode = "201", // 상태 코드
            description = "생성 성공", // 설명
            content = {
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
            }
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content), // 본문 없음
        @ApiResponse(responseCode = "500", description = "서버 내부 오류") // 서버 예외
    })
    public ResponseEntity<Message> create( // 메시지 생성 엔드포인트
        @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,// 멀티파트 중 JSON 파트 바인딩
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments // 멀티파트 중 파일 리스트(선택)
    ) { // 메서드 시작
        List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(
                attachments) // 첨부파일 리스트 null 안전 처리
            .map(files -> files.stream() // 파일 스트림 생성
                .map(file -> { // 각 파일을
                    try { // 바이트 추출 중 예외 처리
                        return new BinaryContentCreateRequest( // 바이너리 DTO로 변환
                            file.getOriginalFilename(), // 원본 파일명
                            file.getContentType(), // MIME 타입
                            file.getBytes() // 파일 바이트
                        ); // 생성자 종료
                    } catch (IOException e) { // IO 예외 발생 시
                        throw new RuntimeException(e); // 런타임 예외로 래핑
                    } // catch 종료
                }) // map 종료
                .toList()) // 스트림을 리스트로 수집
            .orElse(new ArrayList<>()); // 첨부가 없으면 빈 리스트 사용
        Message createdMessage = messageService.create(messageCreateRequest, attachmentRequests); // 서비스 호출로 메시지 생성
        return ResponseEntity // 응답 빌더 시작
            .status(HttpStatus.CREATED) // 201 Created 상태코드
            .body(createdMessage); // 생성된 메시지 본문 반환
    } // create 종료

    @PatchMapping( // 수정은 PUT 메서드로 명시
        path = "/{messageId}", // 기존 update 라우트 유지 + 식별자는 PathVariable로 적용
        consumes = MediaType.APPLICATION_JSON_VALUE, // 요청은 JSON
        produces = MediaType.APPLICATION_JSON_VALUE // 응답은 JSON
    )
    @Operation( // Swagger: 엔드포인트 요약/설명
        summary = "메시지 수정", // 요약
        description = "메시지 식별자와 수정 요청 본문(JSON)을 받아 메시지를 수정합니다." // 설명
        , operationId = "updateMessage"
    )
    @ApiResponses({ // Swagger: 가능한 응답 정의
        @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = {
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
            }
        ),
        @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없음", content = {
            @Content,
            @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
        }
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 본문"), // 유효성 실패 등
        @ApiResponse(responseCode = "500", description = "서버 내부 오류") // 서버 예외
    })
    public ResponseEntity<Message> update( // 메시지 수정 엔드포인트
        @PathVariable("messageId") UUID messageId, // 경로 변수에서 메시지 ID 추출
        @RequestBody MessageUpdateRequest request // 요청 본문(JSON) 바인딩
    ) { // 메서드 시작
        Message updatedMessage = messageService.update(messageId, request); // 서비스 호출로 메시지 수정
        return ResponseEntity // 응답 빌더 시작
            .status(HttpStatus.OK) // 200 OK 상태코드
            .body(updatedMessage); // 수정된 메시지 본문 반환
    } // update 종료

    @DeleteMapping( // 삭제는 DELETE 메서드로 명시
        path = "/{messageId}" // 식별자는 PathVariable로 적용
    )
    @Operation( // Swagger: 엔드포인트 요약/설명
        summary = "메시지 삭제", // 요약
        description = "메시지 식별자(UUID)를 받아 해당 메시지를 삭제합니다." // 설명
        , operationId = "deleteMessage"
    )
    @ApiResponses({ // Swagger: 가능한 응답 정의
        @ApiResponse(
            responseCode = "204",
            description = "삭제 성공",
            content = {
                @Content,
                @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
            }
        ),
        @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없음",
            content = {
            @Content,
            @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
        }),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류") // 서버 예외
    })
    public ResponseEntity<Void> delete( // 메시지 삭제 엔드포인트
        @PathVariable("messageId") UUID messageId // 경로 변수에서 메시지 ID 추출
    ) { // 메서드 시작
        messageService.delete(messageId); // 서비스 호출로 메시지 삭제
        return ResponseEntity // 응답 빌더 시작
            .status(HttpStatus.NO_CONTENT) // 204 No Content 상태코드
            .build(); // 본문 없이 응답
    } // delete 종료

    @GetMapping( // 조회는 GET 메서드로 명시
        produces = MediaType.APPLICATION_JSON_VALUE // 응답은 JSON
    )
    @Operation( // Swagger: 엔드포인트 요약/설명
        summary = "채널별 메시지 목록 조회", // 요약
        description = "채널 식별자(UUID)로 해당 채널의 메시지 목록을 조회합니다.", // 설명
        operationId = "findAllByChannelId",
        parameters = {
            @Parameter(
                name = "channelId",
                description = "조회할 채널 ID",
                required = true,
                in = ParameterIn.QUERY,
                schema = @Schema(type = "string", format = "uuid")
            )
        }
    )
    @ApiResponses({ // Swagger: 가능한 응답 정의
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = {
            @Content(schema = @Schema(implementation = Message.class)),
            @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
        }
        ),
        @ApiResponse(responseCode = "400", description = "요청 파라미터 형식 오류(예: UUID 파싱 실패)"),
        @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<Message>> findAllByChannelId( // 채널별 메시지 목록 조회 엔드포인트
        @Parameter(description = "조회할 채널 ID", required = true) // ★ 문서에 필수로 노출
        @RequestParam(name = "channelId", required = true) UUID channelId // ★ 필수 쿼리 파라미터
    ) { // 메서드 시작
        List<Message> messages = messageService.findAllByChannelId(channelId); // 서비스 호출로 목록 조회
        return ResponseEntity // 응답 빌더 시작
            .status(HttpStatus.OK) // 200 OK 상태코드
            .body(messages); // 조회 결과 본문 반환
    } // findAllByChannelId 종료
} // 클래스 종료

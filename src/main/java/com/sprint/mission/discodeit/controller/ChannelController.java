package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// ===== Swagger/OpenAPI 애너테이션 =====
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
// ====================================

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
@Tag(
    name = "Channels",
    description = "공개/비공개 채널 생성·수정·삭제·조회 API"
)
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping(
        path = "/public",
        consumes = "application/json",
        produces = "application/json"
    )
    @Operation(summary = "공개 채널 생성", description = "공개 채널을 생성합니다.", operationId = "createPublicChannel")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "생성 성공",
            content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
            @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
        }
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 본문"), // 유효성 실패 등
        @ApiResponse(responseCode = "500", description = "서버 내부 오류") // 서버 오류
    })
    public ResponseEntity<Channel> createPublic(@RequestBody PublicChannelCreateRequest request) {
        Channel createdChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @PostMapping(
        path = "/private",
        consumes = "application/json",
        produces = "application/json"
    )
    @Operation(summary = "비공개 채널 생성", description = "비공개 채널을 생성합니다.", operationId = "createPrivateChannel")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "생성 성공",
            content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
            @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
        }
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 본문"), // 유효성 실패 등
        @ApiResponse(responseCode = "500", description = "서버 내부 오류") // 서버 오류
    })
    public ResponseEntity<Channel> createPrivate(@RequestBody PrivateChannelCreateRequest request) {
        Channel createdChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @PatchMapping(
        path = "/{channelId}",
        consumes = "application/json",
        produces = "application/json"
    )
    @Operation(summary = "채널 수정", description = "채널 정보를 수정합니다.", operationId = "updateChannel")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = {
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
            }
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 본문",
            content = {
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                @Content(mediaType = MediaType.ALL_VALUE)
        }), // 유효성 실패 등
        @ApiResponse(responseCode = "404", description = "대상 리소스를 찾을 수 없음",
            content = {
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                @Content(mediaType = MediaType.ALL_VALUE)
        }), // 없는 채널
        @ApiResponse(responseCode = "500", description = "서버 내부 오류") // 서버 오류
    })
    public ResponseEntity<Channel> update(
        @PathVariable("channelId") UUID channelId,
        @RequestBody PublicChannelUpdateRequest request
    ) {
        Channel udpatedChannel = channelService.update(channelId, request);
        return ResponseEntity.status(HttpStatus.OK).body(udpatedChannel);
    }

    @DeleteMapping(path = "/{channelId}")
    @Operation(summary = "채널 삭제", description = "채널을 삭제합니다.", operationId = "deleteChannel")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"), // 본문 없이 204
        @ApiResponse(responseCode = "404", description = "대상 리소스를 찾을 수 없음",
            content = {
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
            }), // 없는 채널
        @ApiResponse(responseCode = "500", description = "서버 내부 오류") // 서버 오류
    })
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(produces = "application/json")
    @Operation(summary = "사용자 채널 조회", description = "사용자 UUID로 채널 목록을 조회합니다.", operationId = "findAllChannelsByUserId")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = {
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
            }
        )
    })
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }
}

package com.sprint.mission.discodeit.notification.presentation;

import com.sprint.mission.discodeit.global.error.ErrorResponse;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.notification.presentation.dto.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notification", description = "알림 API")
public interface NotificationControllerDocs {

    @Operation(summary = "알림 목록 조회", description = "현재 로그인한 사용자의 알림 목록을 조회합니다.")
    @ApiResponse(
        responseCode = "200",
        description = "알림 목록 조회 성공",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class))
        )
    )
    @ApiResponse(
        responseCode = "401",
        description = "인증되지 않은 요청",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    List<NotificationDto> findAll(@Parameter(hidden = true) DiscodeitUserDetails userDetails);

    @Operation(summary = "알림 확인", description = "특정 알림을 읽음 처리합니다.")
    @ApiResponse(responseCode = "204", description = "알림 확인 성공")
    @ApiResponse(
        responseCode = "401",
        description = "인증되지 않은 요청",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    @ApiResponse(
        responseCode = "403",
        description = "본인의 알림만 확인할 수 있습니다",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "알림을 찾을 수 없음",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    void check(
        @Parameter(hidden = true) DiscodeitUserDetails userDetails,
        @Parameter(description = "확인할 알림 ID") UUID notificationId
    );
}

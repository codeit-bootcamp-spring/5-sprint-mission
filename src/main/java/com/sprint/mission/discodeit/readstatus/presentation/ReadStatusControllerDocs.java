package com.sprint.mission.discodeit.readstatus.presentation;

import com.sprint.mission.discodeit.global.error.ErrorResponse;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusDto;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "읽음 상태 API")
public interface ReadStatusControllerDocs {

    @Operation(summary = "현재 로그인한 User의 Message 읽음 상태 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "Message 읽음 상태 목록 조회 성공",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ReadStatusDto.class)
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
    List<ReadStatusDto> findAllByUserId(@Parameter(hidden = true) DiscodeitUserDetails userDetails);

    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponse(
        responseCode = "201",
        description = "Message 읽음 상태가 성공적으로 생성됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ReadStatusDto.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Request body가 유효하지 않음",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "invalidField",
                    description = "Body Field 값이 유효하지 않음",
                    value = """
                        {
                           "timestamp": "2025-09-04T06:15:35.838780Z",
                           "code": "INVALID_BODY_VALUE",
                           "message": "요청 본문 값이 유효하지 않습니다.",
                           "details": {
                             "path": "/api/readStatuses",
                             "fieldErrors": [
                               {
                                 "field": "channelId",
                                 "rejected": null,
                                 "message": "널이어서는 안됩니다"
                               },
                               {
                                 "field": "lastReadAt",
                                 "rejected": null,
                                 "message": "널이어서는 안됩니다"
                               }
                             ],
                             "method": "POST"
                           },
                           "exceptionType": "MethodArgumentNotValidException",
                           "status": 400,
                           "requestId": "07209471-1e0c-4f38-869f-610ee09afb31"
                         }
                        """
                ),
                @ExampleObject(
                    name = "invalidJson",
                    description = "JSON parsing 실패",
                    value = """
                        {
                          "timestamp": "2025-09-04T06:34:11.634992Z",
                          "code": "INVALID_JSON",
                          "message": "요청 본문을 읽을 수 없습니다. JSON 형식과 필드 타입을 확인해주세요.",
                          "details": {
                            "path": "/api/readStatuses",
                            "method": "POST",
                            "cause": "Unexpected character ('}' (code 125)): was expecting double-quote to start field name\\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 5, column: 1]"
                          },
                          "exceptionType": "HttpMessageNotReadableException",
                          "status": 400,
                          "requestId": "024c0ca8-b707-4113-b0a2-edef67fe1772"
                        }
                        """
                )
            }
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
    @ApiResponse(
        responseCode = "404",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "channelNotFound",
                    description = "Channel을 찾을 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-04T06:34:46.594489Z",
                          "code": "CHANNEL_NOT_FOUND",
                          "message": "채널을 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/readStatuses",
                            "method": "POST"
                          },
                          "exceptionType": "ChannelNotFoundException",
                          "status": 404,
                          "requestId": "fb9baee8-b8f9-4e95-8417-459814cf5cdd"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "409",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    description = "이미 읽음 상태가 존재함",
                    value = """
                        {
                          "timestamp": "2025-09-04T06:35:04.759575Z",
                          "code": "CONFLICT",
                          "message": "요청이 현재 리소스 상태와 충돌합니다.",
                          "details": {
                            "path": "/api/readStatuses",
                            "method": "POST"
                          },
                          "exceptionType": "DataIntegrityViolationException",
                          "status": 409,
                          "requestId": "b29f0b5d-9952-4335-8eb6-da3aedaa79c1"
                        }
                        """
                )
            }
        )
    )
    ReadStatusDto create(
        @Parameter(hidden = true) DiscodeitUserDetails userDetails,
        ReadStatusCreateRequest request
    );

    @Operation(summary = "Message 읽음 상태 수정")
    @Parameter(
        name = "readStatusId",
        description = "수정할 읽음 상태 ID"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Message 읽음 상태가 성공적으로 수정됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ReadStatusDto.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "invalidParameterType",
                    description = "parameter(readStatusId) 타입이 UUID가 아님",
                    value = """
                        {
                          "timestamp": "2025-09-04T02:19:30.016741Z",
                          "code": "INVALID_PARAMETER_VALUE",
                          "message": "요청 매개변수 값이 유효하지 않습니다.: parameter=readStatusId, value=not-uuid, expectedType=UUID",
                          "details": {
                            "path": "/api/readStatuses/not-uuid",
                            "method": "PATCH"
                          },
                          "exceptionType": "MethodArgumentTypeMismatchException",
                          "status": 400,
                          "requestId": "9271700d-6503-4956-851a-cdad15075631"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "403",
        description = "본인의 읽음 상태만 수정할 수 있습니다",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "readStatusForbidden",
                    description = "본인의 읽음 상태만 수정할 수 있음",
                    value = """
                        {
                          "timestamp": "2025-09-04T06:36:36.374538Z",
                          "code": "READ_STATUS_FORBIDDEN",
                          "message": "본인의 읽음 상태만 수정할 수 있습니다.",
                          "details": {
                            "path": "/api/readStatuses/bc482f77-d3a9-43fd-a272-4da85df4f041",
                            "method": "PATCH"
                          },
                          "exceptionType": "ReadStatusForbiddenException",
                          "status": 403,
                          "requestId": "447352ac-c747-47df-b9f5-a3a03da8c636"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "404",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "readStatusNotFound",
                    description = "Message 읽음 상태를 찾을 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-04T06:36:36.374538Z",
                          "code": "READ_STATUS_NOT_FOUND",
                          "message": "읽음 상태를 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/readStatuses/bc482f77-d3a9-43fd-a272-4da85df4f041",
                            "method": "PATCH"
                          },
                          "exceptionType": "ReadStatusNotFoundException",
                          "status": 404,
                          "requestId": "447352ac-c747-47df-b9f5-a3a03da8c636"
                        }
                        """
                )
            }
        )
    )
    ReadStatusDto update(
        @Parameter(hidden = true) DiscodeitUserDetails userDetails,
        UUID readStatusId,
        ReadStatusUpdateRequest request
    );
}

package com.sprint.mission.discodeit.docs;

import com.sprint.mission.discodeit.controller.advice.ErrorResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus")
@SuppressWarnings("checkstyle:LineLength")
public interface ReadStatusControllerDocs {

    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @Parameter(
        name = "userId",
        description = "조회할 User ID"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Message 읽음 상태 목록 조회 성공",
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
                    description = "parameter(userId) 타입이 UUID가 아님",
                    value = """
                        {
                          "timestamp": "2025-09-04T02:19:30.016741Z",
                          "code": "INVALID_PARAMETER_TYPE",
                          "message": "parameter=userId, value=not-uuid, expectedType=UUID",
                          "details": {
                            "path": "/api/readStatuses",
                            "method": "GET",
                            "query": "userId=not-uuid"
                          },
                          "exceptionType": "MethodArgumentTypeMismatchException",
                          "status": 400,
                          "requestId": "9271700d-6503-4956-851a-cdad15075631"
                        }
                        """
                ),
                @ExampleObject(
                    name = "missingParameter",
                    description = "요청에 parameter(userId)가 포함되지 않음",
                    value = """
                        {
                          "timestamp": "2025-09-04T02:18:20.915845Z",
                          "code": "MISSING_PARAMETER",
                          "message": "missing parameter: userId (required type: UUID)",
                          "details": {
                            "path": "/api/readStatuses",
                            "method": "GET"
                          },
                          "exceptionType": "MissingServletRequestParameterException",
                          "status": 400,
                          "requestId": "8173f429-234d-4e17-919c-946573e23d89"
                        }
                        """
                )
            }
        )
    )
    List<ReadStatusDto> findAllByUserId(UUID userId);

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
                           "message": "Request body value not valid",
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
                               },
                               {
                                 "field": "userId",
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
                          "message": "Unable to read request body, please check JSON format and field type",
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
        responseCode = "404",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "userNotFound",
                    description = "User를 찾을 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-04T06:34:27.703602Z",
                          "code": "RESOURCE_NOT_FOUND",
                          "message": "User with id 4efc344f-350d-48b0-893e-320ef5f8ae62 not found",
                          "details": {
                            "path": "/api/readStatuses",
                            "method": "POST"
                          },
                          "exceptionType": "NotFoundException",
                          "status": 404,
                          "requestId": "9b34eca0-03c5-4606-96bf-99162aaeeebb"
                        }
                        """
                ),
                @ExampleObject(
                    name = "channelNotFound",
                    description = "Channel을 찾을 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-04T06:34:46.594489Z",
                          "code": "RESOURCE_NOT_FOUND",
                          "message": "Channel with id cce7f6a2-f709-4d43-a234-b18c5f43b663 not found",
                          "details": {
                            "path": "/api/readStatuses",
                            "method": "POST"
                          },
                          "exceptionType": "NotFoundException",
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
                          "message": "(user_id, channel_id)=(4efc344f-350d-48b0-893e-320ef5f8ae61, cce7f6a2-f709-4d43-a234-b18c5f43b662) already exists.",
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
    ReadStatusDto create(ReadStatusCreateRequest req);

    @Operation(summary = "Message 읽음 상태 수정")
    @Parameter(
        name = "readStatusId",
        description = "수정할 읽음 상태 ID"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Message 읽음 상태가 성공적으로 생성됨",
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
                          "code": "INVALID_PARAMETER_TYPE",
                          "message": "parameter=readStatusId, value=not-uuid, expectedType=UUID",
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
                          "code": "RESOURCE_NOT_FOUND",
                          "message": "ReadStatus with id bc482f77-d3a9-43fd-a272-4da85df4f041 not found",
                          "details": {
                            "path": "/api/readStatuses/bc482f77-d3a9-43fd-a272-4da85df4f041",
                            "method": "PATCH"
                          },
                          "exceptionType": "NotFoundException",
                          "status": 404,
                          "requestId": "447352ac-c747-47df-b9f5-a3a03da8c636"
                        }
                        """
                )
            }
        )
    )
    ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest req);
}

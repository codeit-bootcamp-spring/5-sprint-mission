package com.sprint.mission.discodeit.message.presentation;

import com.sprint.mission.discodeit.common.presentation.dto.PaginationRequest;
import com.sprint.mission.discodeit.common.presentation.dto.PaginationResponse;
import com.sprint.mission.discodeit.global.error.ErrorResponse;
import com.sprint.mission.discodeit.message.presentation.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.MessageDto;
import com.sprint.mission.discodeit.message.presentation.dto.MessageUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "메시지 API")
public interface MessageControllerDocs {

    @Operation(summary = "Channel의 Message 목록 조회")
    @Parameters({
        @Parameter(
            name = "channelId",
            description = "조회할 Channel ID"
        ),
        @Parameter(
            name = "cursor",
            description = "페이징 커서 정보"
        ),
        @Parameter(
            name = "pageable",
            description = "페이징 정보",
            schema = @Schema(implementation = PaginationRequest.class),
            example = """
                {
                  "size": 50,
                  "sort": "createdAt,desc"
                }
                """
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "Channel 목록 조회 성공",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PaginationResponse.class),
            examples = @ExampleObject("""
                {
                  "content": [
                    {
                      "id": "e547b966-51cb-4e5a-9df0-f8996da38839",
                      "createdAt": "2025-09-04T09:40:04.880177Z",
                      "updatedAt": "2025-09-04T09:40:04.880177Z",
                      "content": "",
                      "channelId": "cce7f6a2-f709-4d43-a234-b18c5f43b662",
                      "author": {
                        "id": "0d5d2d3e-b3d8-48b3-b880-3711bd8c520f",
                        "username": "test",
                        "email": "test@example.com",
                        "profile": null,
                        "online": true
                      },
                      "attachments": [
                        {
                          "id": "d4c8c572-70c7-46cd-9cc8-403730dc62d4",
                          "fileName": "attachment.png",
                          "size": 14123,
                          "contentType": "image/png"
                        }
                      ]
                    },
                    {
                      "id": "27cdfb8e-7732-433f-9ddf-a66e4146e8d4",
                      "createdAt": "2025-09-04T09:27:55.378176Z",
                      "updatedAt": "2025-09-04T09:27:55.378176Z",
                      "content": "살려주세요",
                      "channelId": "cce7f6a2-f709-4d43-a234-b18c5f43b662",
                      "author": {
                        "id": "661608b2-469b-457f-b22f-8c2290f8d80f",
                        "username": "test2",
                        "email": "test2@example.com",
                        "profile": {
                          "id": "3a44bc04-e179-4533-bcf1-cfdc3aa86a4a",
                          "fileName": "profile2.webp",
                          "size": 12529,
                          "contentType": "image/webp"
                        },
                        "online": false
                      },
                      "attachments": null
                    }
                  ],
                  "nextCursor": "2025-09-04T09:27:55.378176Z",
                  "size": 2,
                  "hasNext": true
                }
                """
            )
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
                    description = "parameter(channelId) 타입이 유효하지 않음",
                    value = """
                        {
                          "timestamp": "2025-09-04T09:52:02.420208Z",
                          "code": "INVALID_PARAMETER_VALUE",
                          "message": "요청 매개변수 값이 유효하지 않습니다.: parameter=channelId, value=not-uuid, expectedType=UUID",
                          "details": {
                            "path": "/api/messages",
                            "method": "GET",
                            "query": "channelId=not-uuid"
                          },
                          "exceptionType": "MethodArgumentTypeMismatchException",
                          "status": 400,
                          "requestId": "e2d4906d-670a-40ad-8330-0c44c849f177"
                        }
                        """
                ),
                @ExampleObject(
                    name = "missingParameter",
                    description = "요청에 parameter(channelId)가 포함되지 않음",
                    value = """
                        {
                          "timestamp": "2025-09-04T09:48:50.140730Z",
                          "code": "MISSING_PARAMETER",
                          "message": "요청 매개변수가 누락되었습니다.: channelId (필요한 매개변수: UUID)",
                          "details": {
                            "path": "/api/messages",
                            "method": "GET"
                          },
                          "exceptionType": "MissingServletRequestParameterException",
                          "status": 400,
                          "requestId": "6e339446-019d-4a5b-a2dd-17ebb405936a"
                        }
                        """
                )
            }
        )
    )
    PaginationResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, PaginationRequest paginationRequest);

    @Operation(summary = "Message 생성")
    @RequestBody(
        required = true,
        content = @Content(
            schema = @Schema(
                type = "object",
                requiredProperties = {"messageCreateRequest"},
                properties = {
                    @StringToClassMapItem(
                        key = "messageCreateRequest",
                        value = MessageCreateRequest.class
                    ),
                    @StringToClassMapItem(
                        key = "attachments",
                        value = MultipartFile[].class
                    )
                }
            ),
            encoding = {
                @Encoding(
                    name = "messageCreateRequest",
                    contentType = "application/json"
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "201",
        description = "Message가 성공적으로 생성됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = MessageDto.class),
            examples = @ExampleObject("""
                {
                  "id": "e547b966-51cb-4e5a-9df0-f8996da38839",
                  "createdAt": "2025-09-04T09:40:04.880177Z",
                  "updatedAt": "2025-09-04T09:40:04.880177Z",
                  "content": "",
                  "channelId": "cce7f6a2-f709-4d43-a234-b18c5f43b662",
                  "author": {
                    "id": "0d5d2d3e-b3d8-48b3-b880-3711bd8c520f",
                    "username": "test",
                    "email": "test@example.com",
                    "profile": null,
                    "online": true
                  },
                  "attachments": [
                    {
                      "id": "d4c8c572-70c7-46cd-9cc8-403730dc62d4",
                      "fileName": "attachment.png",
                      "size": 14123,
                      "contentType": "image/png"
                    }
                  ]
                }
                """
            )
        )
    )
    @ApiResponse(
        responseCode = "400",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "invalidField",
                    description = "Body Field 값이 유효하지 않음",
                    value = """
                        {
                           "timestamp": "2025-09-03T15:44:53.822173Z",
                           "code": "INVALID_BODY_VALUE",
                           "message": "요청 본문 값이 유효하지 않습니다.",
                           "details": {
                             "path": "/api/messages",
                             "fieldErrors": [
                               {
                                 "field": "channelId",
                                 "rejected": null,
                                 "message": "널이어서는 안됩니다"
                               },
                               {
                                 "field": "authorId",
                                 "rejected": null,
                                 "message": "널이어서는 안됩니다"
                               },
                               {
                                 "field": "content",
                                 "rejected": "...",
                                 "message": "크기가 0에서 2000 사이여야 합니다"
                               }
                             ],
                             "method": "POST"
                           },
                           "exceptionType": "MethodArgumentNotValidException",
                           "status": 400,
                           "requestId": "fd58b987-702b-4bf7-a1de-55506eb0babc"
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
                    name = "channelNotFound",
                    description = "Channel을 찾을 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-04T04:23:03.208002Z",
                          "code": "CHANNEL_NOT_FOUND",
                          "message": "채널을 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/messages",
                            "method": "POST"
                          },
                          "exceptionType": "ChannelNotFoundException",
                          "status": 404,
                          "requestId": "f1b4cb43-3c8d-44b4-933e-91a17662623f"
                        }
                        """
                ),
                @ExampleObject(
                    name = "userNotFound",
                    description = "User를 찾을 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-04T04:23:03.208002Z",
                          "code": "USER_NOT_FOUND",
                          "message": "사용자를 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/messages",
                            "method": "POST"
                          },
                          "exceptionType": "UserNotFoundException",
                          "status": 404,
                          "requestId": "f1b4cb43-3c8d-44b4-933e-91a17662623f"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "413",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "payloadTooLarge",
                    description = "첨부 파일 용량 초과",
                    value = """
                        {
                          "timestamp": "2025-09-04T04:23:03.208002Z",
                          "code": "PAYLOAD_TOO_LARGE",
                          "message": "요청 본문 크기가 너무 큽니다.",
                          "details": {
                            "path": "/api/messages",
                            "method": "POST"
                          },
                          "exceptionType": "MaxUploadSizeExceededException",
                          "status": 413,
                          "requestId": "f1b4cb43-3c8d-44b4-933e-91a17662623f"
                        }
                        """
                )
            }
        )
    )
    MessageDto create(MessageCreateRequest req, List<MultipartFile> attachments);

    @Operation(summary = "Message 삭제")
    @Parameter(
        name = "messageId",
        description = "삭제할 Message ID"
    )
    @ApiResponse(
        responseCode = "204",
        description = "Message가 성공적으로 삭제됨"
    )
    @ApiResponse(
        responseCode = "403",
        description = "Message를 삭제할 권한이 없음",
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
                    name = "messageNotFound",
                    description = "Message를 찾을 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-04T04:23:03.208002Z",
                          "code": "MESSAGE_NOT_FOUND",
                          "message": "메시지를 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/messages/c9b4f154-145d-4372-ab9a-ca030e13b327",
                            "method": "DELETE"
                          },
                          "exceptionType": "MessageNotFoundException",
                          "status": 404,
                          "requestId": "f1b4cb43-3c8d-44b4-933e-91a17662623f"
                        }
                        """
                )
            }
        )
    )
    void deleteById(UUID messageId);

    @Operation(summary = "Message 내용 수정")
    @Parameter(
        name = "messageId",
        description = "수정할 Message ID"
    )
    @RequestBody(
        required = true,
        content = @Content(
            schema = @Schema(implementation = MessageUpdateRequest.class)
        )
    )
    @ApiResponse(
        responseCode = "200",
        description = "Message가 성공적으로 수정됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = MessageDto.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "invalidField",
                    description = "Body Field 값이 유효하지 않음",
                    value = """
                        {
                           "timestamp": "2025-09-03T15:44:53.822173Z",
                           "code": "INVALID_BODY_VALUE",
                           "message": "요청 본문 값이 유효하지 않습니다.",
                           "details": {
                             "path": "/api/messages/c9b4f154-145d-4372-ab9a-ca030e13b327",
                             "fieldErrors": [
                               {
                                 "field": "newContent",
                                 "rejected": "...",
                                 "message": "크기가 0에서 2000 사이여야 합니다"
                               }
                             ],
                             "method": "PATCH"
                           },
                           "exceptionType": "MethodArgumentNotValidException",
                           "status": 400,
                           "requestId": "fd58b987-702b-4bf7-a1de-55506eb0babc"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "403",
        description = "Message를 수정할 권한이 없음",
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
                    name = "messageNotFound",
                    description = "Message를 찾을 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-04T04:23:03.208002Z",
                          "code": "MESSAGE_NOT_FOUND",
                          "message": "메시지를 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/messages/c9b4f154-145d-4372-ab9a-ca030e13b327",
                            "method": "PATCH"
                          },
                          "exceptionType": "MessageNotFoundException",
                          "status": 404,
                          "requestId": "f1b4cb43-3c8d-44b4-933e-91a17662623f"
                        }
                        """
                )
            }
        )
    )
    MessageDto update(UUID messageId, MessageUpdateRequest req);
}

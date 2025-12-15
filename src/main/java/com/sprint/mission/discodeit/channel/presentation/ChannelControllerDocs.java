package com.sprint.mission.discodeit.channel.presentation;

import com.sprint.mission.discodeit.channel.presentation.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.presentation.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.global.error.ErrorResponse;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "채널 API")
public interface ChannelControllerDocs {

    @Operation(summary = "현재 로그인한 User가 참여 중인 Channel 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "Channel 목록 조회 성공",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)),
            examples = @ExampleObject(
                value = """
                    [
                      {
                          "id": "7e297daa-aeec-47ae-b1e0-c63f7a8f9824",
                          "type": "PRIVATE",
                          "name": null,
                          "description": null,
                          "participants": [
                            {
                              "id": "dd210d1a-ebe6-499f-8936-859790fd3716",
                              "username": "test",
                              "email": "test@example.com",
                              "profile": {
                                "id": "957a0ce6-8fde-4397-bb9a-446dcb49578e",
                                "fileName": "profile.png",
                                "size": 32122,
                                "contentType": "image/png"
                              },
                              "online": false
                            },
                            {
                              "id": "8fb5dd71-b7a0-4b5d-bf37-ea410473c618",
                              "username": "test2",
                              "email": "test2@example.com",
                              "profile": {
                                "id": "3a44bc04-e179-4533-bcf1-cfdc3aa86a4a",
                                "fileName": "profile2.webp",
                                "size": 12529,
                                "contentType": "image/webp"
                              },
                              "online": true
                            }
                          ],
                          "lastMessageAt": null
                        },
                      {
                        "id": "6fb818cd-44f3-4289-bb83-fe49741c4de7",
                        "type": "PUBLIC",
                        "name": "Channel name",
                        "description": "Channel description",
                        "participants": [],
                        "lastMessageAt": null
                      }
                    ]
                    """
            )
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
    List<ChannelDto> findAll(@Parameter(hidden = true) DiscodeitUserDetails userDetails);

    @Operation(summary = "Public Channel 생성")
    @RequestBody(
        required = true,
        content = @Content(
            schema = @Schema(implementation = PublicChannelCreateRequest.class)
        )
    )
    @ApiResponse(
        responseCode = "201",
        description = "Public Channel이 성공적으로 생성됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ChannelDto.class),
            examples = @ExampleObject(
                value = """
                    {
                      "id": "6fb818cd-44f3-4289-bb83-fe49741c4de7",
                      "type": "PUBLIC",
                      "name": "Channel name",
                      "description": "Channel description",
                      "participants": [],
                      "lastMessageAt": null
                    }
                    """
            )
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "invalidField",
                    description = "Body field 값이 유효하지 않음",
                    value = """
                        {
                          "timestamp": "2025-09-04T02:28:35.399138Z",
                          "code": "INVALID_BODY_VALUE",
                          "message": "요청 본문 값이 유효하지 않습니다.",
                          "details": {
                            "path": "/api/channels/public",
                            "fieldErrors": [
                              {
                                "field": "name",
                                "rejected": null,
                                "message": "공백일 수 없습니다"
                              }
                            ],
                            "method": "POST"
                          },
                          "exceptionType": "MethodArgumentNotValidException",
                          "status": 400,
                          "requestId": "5246f10a-a1f3-42dc-afb3-629ec2371e87"
                        }
                        """
                ),
                @ExampleObject(
                    name = "invalidJson",
                    description = "JSON parsing 실패",
                    value = """
                        {
                          "timestamp": "2025-09-04T02:30:41.756840Z",
                          "code": "INVALID_JSON",
                          "message": "요청 본문을 읽을 수 없습니다. JSON 형식과 필드 타입을 확인해주세요.",
                          "details": {
                            "path": "/api/channels/public",
                            "method": "POST",
                            "cause": "Unexpected character ('}' (code 125)): was expecting double-quote to start field name\\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 3, column: 1]"
                          },
                          "exceptionType": "HttpMessageNotReadableException",
                          "status": 400,
                          "requestId": "816d2eb6-4ef0-4285-bc98-9b6c2c425ccf"
                        }
                        """
                )
            }
        )
    )
    ChannelDto createPublic(PublicChannelCreateRequest req);

    @Operation(
        summary = "Private Channel 생성",
        description = """
            비공개 채널을 생성합니다.
            
            - 참가자는 2~10명 사이여야 합니다.
            - 참가자가 2명인 DM 채널은 중복해서 생성할 수 없습니다.
            """
    )
    @RequestBody(
        required = true,
        content = @Content(
            schema = @Schema(implementation = PrivateChannelCreateRequest.class)
        )
    )
    @ApiResponse(
        responseCode = "201",
        description = "Private Channel이 성공적으로 생성됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ChannelDto.class),
            examples = @ExampleObject(
                value = """
                        {
                          "id": "7e297daa-aeec-47ae-b1e0-c63f7a8f9824",
                          "type": "PRIVATE",
                          "name": null,
                          "description": null,
                          "participants": [
                            {
                              "id": "dd210d1a-ebe6-499f-8936-859790fd3716",
                              "username": "test",
                              "email": "test@example.com",
                              "profile": {
                                "id": "957a0ce6-8fde-4397-bb9a-446dcb49578e",
                                "fileName": "profile.png",
                                "size": 32122,
                                "contentType": "image/png"
                              },
                              "online": false
                            },
                            {
                              "id": "8fb5dd71-b7a0-4b5d-bf37-ea410473c618",
                              "username": "test2",
                              "email": "test2@example.com",
                              "profile": {
                                "id": "3a44bc04-e179-4533-bcf1-cfdc3aa86a4a",
                                "fileName": "profile2.webp",
                                "size": 12529,
                                "contentType": "image/webp"
                              },
                              "online": true
                            }
                          ],
                          "lastMessageAt": null
                        }
                    """
            )
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "invalidField",
                    description = "Body field 값이 유효하지 않음",
                    value = """
                        {
                          "timestamp": "2025-09-04T03:21:34.614436Z",
                          "code": "INVALID_BODY_VALUE",
                          "message": "요청 본문 값이 유효하지 않습니다.",
                          "details": {
                            "path": "/api/channels/private",
                            "fieldErrors": [
                              {
                                "field": "participantIds",
                                "rejected": null,
                                "message": "널이어서는 안됩니다"
                              },
                              {
                                "field": "participantIds",
                                "rejected": [],
                                "message": "크기가 2에서 10 사이여야 합니다"
                              }
                            ],
                            "method": "POST"
                          },
                          "exceptionType": "MethodArgumentNotValidException",
                          "status": 400,
                          "requestId": "7fee9e49-1c6b-4a4a-b9fc-88c03af9504f"
                        }
                        """
                ),
                @ExampleObject(
                    name = "invalidJson",
                    description = "JSON parsing 실패",
                    value = """
                        {
                          "timestamp": "2025-09-04T03:22:27.277804Z",
                          "code": "INVALID_JSON",
                          "message": "요청 본문을 읽을 수 없습니다. JSON 형식과 필드 타입을 확인해주세요.",
                          "details": {
                            "path": "/api/channels/private",
                            "method": "POST",
                            "cause": "Unexpected character (']' (code 93)): expected a value\\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 5, column: 5]"
                          },
                          "exceptionType": "HttpMessageNotReadableException",
                          "status": 400,
                          "requestId": "3e2bdf2d-674d-4246-82c9-a0236891f383"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "리소스를 찾을 수 없음",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "userNotFound",
                    description = "participantIds에 존재하지 않는 User의 id가 포함됨",
                    value = """
                        {
                          "timestamp": "2025-09-04T04:23:03.208002Z",
                          "code": "USERS_NOT_FOUND",
                          "message": "일부 사용자를 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/channels/private",
                            "method": "POST"
                          },
                          "exceptionType": "UsersNotFoundException",
                          "status": 404,
                          "requestId": "f1b4cb43-3c8d-44b4-933e-91a17662623f"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "409",
        description = "리소스 충돌",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "duplicatePrivateChannel",
                    description = "1:1 DM 채널이 이미 존재함",
                    value = """
                        {
                          "timestamp": "2025-09-04T03:25:45.708692Z",
                          "code": "DUPLICATE_PRIVATE_CHANNEL",
                          "message": "이미 존재하는 개인 채널입니다.",
                          "details": {
                            "path": "/api/channels/private",
                            "method": "POST"
                          },
                          "exceptionType": "DuplicateChannelException",
                          "status": 409,
                          "requestId": "b52caf04-c71f-4446-8a18-8a1f485d6e1c"
                        }
                        """
                )
            }
        )
    )
    ChannelDto createPrivate(PrivateChannelCreateRequest req);

    @Operation(summary = "Channel 삭제")
    @Parameter(
        name = "channelId",
        description = "삭제할 Channel ID"
    )
    @ApiResponse(
        responseCode = "204",
        description = "Channel이 성공적으로 삭제됨"
    )
    @ApiResponse(
        responseCode = "404",
        description = "채널을 찾을 수 없음",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "channelNotFound",
                    description = "Channel을 찾을 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-04T02:48:55.109967Z",
                          "code": "CHANNEL_NOT_FOUND",
                          "message": "채널을 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/channels/d1d96548-3f49-4bef-9a61-52f723b146d1",
                            "method": "DELETE"
                          },
                          "exceptionType": "ChannelNotFoundException",
                          "status": 404,
                          "requestId": "4d8855d4-439b-45a4-a7da-5ab049c46e14"
                        }
                        """
                )
            }
        )
    )
    void deleteById(UUID channelId);

    @Operation(summary = "Channel 정보 수정")
    @Parameter(
        name = "channelId",
        description = "수정할 Channel ID"
    )
    @RequestBody(
        required = true,
        content = @Content(
            schema = @Schema(implementation = PublicChannelUpdateRequest.class)
        )
    )
    @ApiResponse(
        responseCode = "200",
        description = "Channel 정보가 성공적으로 수정됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ChannelDto.class),
            examples = @ExampleObject(
                value = """
                    {
                      "id": "6fb818cd-44f3-4289-bb83-fe49741c4de7",
                      "type": "PUBLIC",
                      "name": "New channel name",
                      "description": "New channel description",
                      "participants": [],
                      "lastMessageAt": "2025-09-03T08:44:32.921900Z"
                    }
                    """
            )
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "invalidJson",
                    description = "JSON parsing 실패",
                    value = """
                        {
                          "timestamp": "2025-09-04T02:44:16.013658Z",
                          "code": "INVALID_JSON",
                          "message": "요청 본문을 읽을 수 없습니다. JSON 형식과 필드 타입을 확인해주세요.",
                          "details": {
                            "path": "/api/channels/6fb818cd-44f3-4289-bb83-fe49741c4de5",
                            "method": "PATCH",
                            "cause": "Unexpected character ('}' (code 125)): was expecting double-quote to start field name\\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 4, column: 1]"
                          },
                          "exceptionType": "HttpMessageNotReadableException",
                          "status": 400,
                          "requestId": "db90d1d3-2915-47e4-8a0d-47490a17c1d2"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "403",
        description = "접근 권한 없음",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "updateNotAllowed",
                    description = "Private channel은 수정할 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-04T02:46:05.486359Z",
                          "code": "PRIVATE_CHANNEL_UPDATE",
                          "message": "개인 채널은 수정할 수 없습니다.",
                          "details": {
                            "path": "/api/channels/d1d96548-3f49-4bef-9a61-52f723b146de",
                            "method": "PATCH"
                          },
                          "exceptionType": "PrivateChannelUpdateException",
                          "status": 403,
                          "requestId": "c331901b-2828-45e5-aa5c-d5b1e66175ef"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "채널을 찾을 수 없음",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "channelNotFound",
                    description = "Channel을 찾을 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-04T02:43:49.646270Z",
                          "code": "CHANNEL_NOT_FOUND",
                          "message": "채널을 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/channels/6fb818cd-44f3-4289-bb83-fe49741c4de5",
                            "method": "PATCH"
                          },
                          "exceptionType": "ChannelNotFoundException",
                          "status": 404,
                          "requestId": "3fc8c881-61d2-48eb-bb83-ae58d5729fc8"
                        }
                        """
                )
            }
        )
    )
    ChannelDto update(UUID channelId, PublicChannelUpdateRequest req);
}

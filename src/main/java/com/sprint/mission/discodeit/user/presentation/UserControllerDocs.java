package com.sprint.mission.discodeit.user.presentation;

import com.sprint.mission.discodeit.global.error.ErrorResponse;
import com.sprint.mission.discodeit.user.presentation.dto.UserCreateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import com.sprint.mission.discodeit.user.presentation.dto.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "사용자 API")
public interface UserControllerDocs {

    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "User 목록 조회 성공",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = UserDto.class)),
            examples = @ExampleObject(
                value = """
                    [
                      {
                        "id": "1adf8c04-6676-4e76-b9c1-d2234642f4a7",
                        "username": "test",
                        "email": "test@example.com",
                        "profile": {
                          "id": "d52ed597-d15e-4436-ba9c-2a3f9ea627bf",
                          "fileName": "profile.png",
                          "size": 12529,
                          "contentType": "image/png"
                        },
                        "online": false
                      },
                      {
                        "id": "7e164171-71da-46b6-9814-c5c60b9fb6df",
                        "username": "test2",
                        "email": "test2@example.com",
                        "profile": null,
                        "online": true
                      }
                    ]
                    """
            )
        )
    )
    List<UserDto> findAll();

    @Operation(summary = "User 등록")
    @RequestBody(
        required = true,
        content = @Content(
            mediaType = "multipart/form-data",
            schema = @Schema(
                type = "object",
                requiredProperties = {"userCreateRequest"},
                properties = {
                    @StringToClassMapItem(
                        key = "userCreateRequest",
                        value = UserCreateRequest.class
                    ),
                    @StringToClassMapItem(
                        key = "profile",
                        value = MultipartFile.class
                    )
                }
            ),
            encoding = {
                @Encoding(
                    name = "userCreateRequest",
                    contentType = "application/json"
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "201",
        description = "User가 성공적으로 생성됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class)
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
                    description = "userCreateRequest Field 값이 유효하지 않음",
                    value = """
                        {
                           "timestamp": "2025-09-03T08:52:56.103334Z",
                           "code": "INVALID_BODY_VALUE",
                           "message": "요청 본문 값이 유효하지 않습니다.",
                           "details": {
                             "path": "/api/users",
                             "fieldErrors": [
                               {
                                 "field": "email",
                                 "rejected": null,
                                 "message": "공백일 수 없습니다"
                               },
                               {
                                 "field": "password",
                                 "rejected": null,
                                 "message": "공백일 수 없습니다"
                               },
                               {
                                 "field": "username",
                                 "rejected": null,
                                 "message": "공백일 수 없습니다"
                               }
                             ],
                             "method": "POST"
                           },
                           "exceptionType": "MethodArgumentNotValidException",
                           "status": 400,
                           "requestId": "0b3d0811-6d90-4a5f-84a1-429f32177173"
                        }
                        """
                ),
                @ExampleObject(
                    name = "invalidJson",
                    description = "JSON parsing 실패",
                    value = """
                        {
                          "timestamp": "2025-09-03T08:04:08.236418Z",
                          "code": "INVALID_JSON",
                          "message": "요청 본문을 읽을 수 없습니다. JSON 형식과 필드 타입을 확인해주세요.",
                          "details": {
                            "path": "/api/users",
                            "method": "POST",
                            "cause": "Unexpected character ('}' (code 125)): was expecting double-quote to start field name\\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 5, column: 1]"
                          },
                          "exceptionType": "HttpMessageNotReadableException",
                          "status": 400,
                          "requestId": "db5ea38a-4127-4700-b2a3-665e88d86f50"
                        }
                        """
                ),
                @ExampleObject(
                    name = "missingUserCreateRequest",
                    description = "요청에 userCreateRequest가 포함되지 않음",
                    value = """
                        {
                          "timestamp": "2025-09-03T08:44:32.921900Z",
                          "code": "MISSING_PART",
                          "message": "요청에 필요한 요소가 포함되어 있지 않습니다. userCreateRequest",
                          "details": {
                            "path": "/api/users",
                            "method": "POST"
                          },
                          "exceptionType": "MissingServletRequestPartException",
                          "status": 400,
                          "requestId": "926750dc-f642-424e-8dbe-0d748fe5ff24"
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
                    name = "duplicateUsername",
                    description = "같은 username을 사용하는 User가 이미 존재함",
                    value = """
                        {
                          "timestamp": "2025-09-03T08:05:41.768283Z",
                          "code": "CONFLICT",
                          "message": "요청이 현재 리소스 상태와 충돌합니다.",
                          "details": {
                            "path": "/api/users",
                            "method": "POST"
                          },
                          "exceptionType": "DataIntegrityViolationException",
                          "status": 409,
                          "requestId": "b842eb4a-f96e-4ce2-9543-13d903fc165f"
                        }
                        """
                ),
                @ExampleObject(
                    name = "duplicateEmail",
                    description = "같은 email을 사용하는 User가 이미 존재함",
                    value = """
                        {
                          "timestamp": "2025-09-03T08:05:41.768283Z",
                          "code": "CONFLICT",
                          "message": "요청이 현재 리소스 상태와 충돌합니다.",
                          "details": {
                            "path": "/api/users",
                            "method": "POST"
                          },
                          "exceptionType": "DataIntegrityViolationException",
                          "status": 409,
                          "requestId": "2b499d4c-c501-4b75-8910-db923933dd52"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "415",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "invalidMediaType",
                    description = "userCreateRequest가 application/json 형식이 아님",
                    value = """
                        {
                          "timestamp": "2025-09-03T09:21:03.545517Z",
                          "code": "UNSUPPORTED_MEDIA_TYPE",
                          "message": "지원하지 않는 미디어 타입입니다. 지원되는 타입들: application/json, application/yaml, application/*+json",
                          "details": {
                            "path": "/api/users",
                            "method": "POST"
                          },
                          "exceptionType": "HttpMediaTypeNotSupportedException",
                          "status": 415,
                          "requestId": "205cd17e-87a4-48bc-b2f0-3a3d12b03b46"
                        }
                        """
                )
            }
        )
    )
    UserDto create(UserCreateRequest request, MultipartFile profile);

    @Operation(summary = "User 삭제")
    @Parameter(
        name = "userId",
        description = "삭제할 User ID"
    )
    @ApiResponse(
        responseCode = "204",
        description = "User가 성공적으로 삭제됨"
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
                           "timestamp": "2025-09-03T09:49:27.703663Z",
                           "code": "INVALID_PARAMETER_VALUE",
                           "message": "요청 매개변수 값이 유효하지 않습니다.: parameter=userId, value=not-uuid, expectedType=UUID",
                           "details": {
                             "path": "/api/users/not-uuid",
                             "method": "DELETE"
                           },
                           "exceptionType": "MethodArgumentTypeMismatchException",
                           "status": 400,
                           "requestId": "7014bf9c-387a-443b-a59b-6c00edfea84b"
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
                          "timestamp": "2025-09-03T09:48:06.598818Z",
                          "code": "USER_NOT_FOUND",
                          "message": "사용자를 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/users/73c8f68b-0e72-4c46-b8e8-b3c2f570bac1",
                            "method": "DELETE"
                          },
                          "exceptionType": "UserNotFoundException",
                          "status": 404,
                          "requestId": "a783f2f8-5340-4746-a7b1-da1adca3ef86"
                        }
                        """
                )
            }
        )
    )
    void deleteById(UUID userId);

    @Operation(summary = "User 정보 수정")
    @Parameter(
        name = "userId",
        description = "수정할 User ID"
    )
    @RequestBody(
        required = true,
        content = @Content(
            mediaType = "multipart/form-data",
            schema = @Schema(
                type = "object",
                properties = {
                    @StringToClassMapItem(
                        key = "userUpdateRequest",
                        value = UserUpdateRequest.class
                    ),
                    @StringToClassMapItem(
                        key = "profile",
                        value = MultipartFile.class
                    )
                }
            ),
            encoding = {
                @Encoding(
                    name = "userUpdateRequest",
                    contentType = "application/json"
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "200",
        description = "User 정보가 성공적으로 수정됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class)
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
                           "timestamp": "2025-09-03T09:49:27.703663Z",
                           "code": "INVALID_PARAMETER_VALUE",
                           "message": "요청 매개변수 값이 유효하지 않습니다.: parameter=userId, value=not-uuid, expectedType=UUID",
                           "details": {
                             "path": "/api/users/not-uuid",
                             "method": "PATCH"
                           },
                           "exceptionType": "MethodArgumentTypeMismatchException",
                           "status": 400,
                           "requestId": "2014bf9c-387a-443b-a59b-6c00edfea84b"
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
                          "timestamp": "2025-09-03T09:48:06.598818Z",
                          "code": "USER_NOT_FOUND",
                          "message": "사용자를 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/users/73c8f68b-0e72-4c46-b8e8-b3c2f570bac1",
                            "method": "PATCH"
                          },
                          "exceptionType": "UserNotFoundException",
                          "status": 404,
                          "requestId": "a783f2f8-5340-4746-a7b1-da1adca3ef86"
                        }
                        """
                )
            }
        )
    )
    UserDto update(
        UUID userId,
        UserUpdateRequest request,
        MultipartFile profile,
        HttpServletRequest servletRequest
    );
}

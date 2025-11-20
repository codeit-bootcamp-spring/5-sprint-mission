package com.sprint.mission.discodeit.docs;

import com.sprint.mission.discodeit.controller.advice.ApiError;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth")
@SuppressWarnings("checkstyle:LineLength")
public interface AuthControllerDocs {

    @Operation(summary = "로그인")
    @ApiResponse(
        responseCode = "200",
        description = "로그인 성공",
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
            schema = @Schema(implementation = ApiError.class),
            examples = {
                @ExampleObject(
                    name = "invalidField",
                    description = "Body Field 값이 유효하지 않음",
                    value = """
                        {
                           "timestamp": "2025-09-03T15:44:53.822173Z",
                           "code": "INVALID_BODY_VALUE",
                           "message": "Request body value not valid",
                           "details": {
                             "path": "/api/auth/login",
                             "fieldErrors": [
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
                           "requestId": "fd58b987-702b-4bf7-a1de-55506eb0babc"
                        }
                        """
                ),
                @ExampleObject(
                    name = "invalidJson",
                    description = "JSON parsing 실패",
                    value = """
                        {
                          "timestamp": "2025-09-03T15:45:44.976824Z",
                          "code": "INVALID_JSON",
                          "message": "Unable to read request body, please check JSON format and field type",
                          "details": {
                            "path": "/api/auth/login",
                            "method": "POST",
                            "cause": "Unexpected character ('}' (code 125)): was expecting double-quote to start field name\\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 4, column: 1]"
                          },
                          "exceptionType": "HttpMessageNotReadableException",
                          "status": 400,
                          "requestId": "e8ce476b-49b2-44a9-ab5b-1440aadb1652"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "401",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ApiError.class),
            examples = {
                @ExampleObject(
                    name = "unauthorized",
                    description = "사용자명 또는 비밀번호가 일치하지 않음",
                    value = """
                        {
                          "timestamp": "2025-09-03T15:47:16.592910Z",
                          "code": "UNAUTHORIZED",
                          "message": "Username or password incorrect",
                          "details": {
                            "path": "/api/auth/login",
                            "method": "POST"
                          },
                          "exceptionType": "UnauthorizedException",
                          "status": 401,
                          "requestId": "5667739b-beb7-490f-953c-89808ae8a09f"
                        }
                        """
                )
            }
        )
    )
    UserDto login(LoginRequest req);
}

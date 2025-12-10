package com.sprint.mission.discodeit.binarycontent.presentation;

import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import com.sprint.mission.discodeit.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "파일 API")
public interface BinaryContentControllerDocs {

    @Operation(summary = "여러 첨부 파일 조회")
    @Parameter(
        name = "binaryContentIds",
        description = "조회할 첨부 파일 ID 목록"
    )
    @ApiResponse(
        responseCode = "200",
        description = "첨부 파일 목록 조회 성공",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = BinaryContentDto.class)),
            examples = @ExampleObject(
                value = """
                    [
                      {
                        "id": "957a0ce6-8fde-4397-bb9a-446dcb49578e",
                        "fileName": "profile.png",
                        "size": 24123,
                        "contentType": "image/png"
                      },
                      {
                        "id": "3a44bc04-e179-4533-bcf1-cfdc3aa86a4a",
                        "fileName": "attachment.webp",
                        "size": 12529,
                        "contentType": "image/webp"
                      }
                    ]
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
                    name = "invalidParameterType",
                    description = "parameter(binaryContentIds) 타입이 UUID가 아님",
                    value = """
                        {
                          "timestamp": "2025-09-03T16:00:37.339821Z",
                          "code": "INVALID_PARAMETER_VALUE",
                          "message": "요청 매개변수 값이 유효하지 않습니다.: parameter=binaryContentIds, value=[Ljava.lang.String;@4df10515, expectedType=Set",
                          "details": {
                            "path": "/api/binaryContents",
                            "method": "GET",
                            "query": "binaryContentIds=not-uuid&binaryContentIds=null"
                          },
                          "exceptionType": "MethodArgumentTypeMismatchException",
                          "status": 400,
                          "requestId": "8b66231c-4d71-4d8a-b4bb-a8bc8cd03567"
                        }
                        """
                ),
                @ExampleObject(
                    name = "missingParameter",
                    description = "요청에 parameter(binaryContentIds)가 포함되지 않음",
                    value = """
                        {
                          "timestamp": "2025-09-03T16:03:12.387990Z",
                          "code": "MISSING_PARAMETER",
                          "message": "요청 매개변수가 누락되었습니다.: binaryContentIds (필요한 매개변수: Set)",
                          "details": {
                            "path": "/api/binaryContents",
                            "method": "GET"
                          },
                          "exceptionType": "MissingServletRequestParameterException",
                          "status": 400,
                          "requestId": "1acda3fe-8cf7-4ca7-90f9-b7b816fa164f"
                        }
                        """
                )
            }
        )
    )
    List<BinaryContentDto> findAllById(Set<UUID> binaryContentIds);

    @Operation(summary = "첨부 파일 조회")
    @Parameter(
        name = "binaryContentId",
        description = "조회할 첨부 파일 ID"
    )
    @ApiResponse(
        responseCode = "200",
        description = "첨부 파일 조회 성공",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BinaryContentDto.class)
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
                    name = "invalidParameterType",
                    description = "parameter(binaryContentId) 타입이 UUID가 아님",
                    value = """
                        {
                          "timestamp": "2025-09-03T16:07:55.585502Z",
                          "code": "INVALID_PARAMETER_VALUE",
                          "message": "요청 매개변수 값이 유효하지 않습니다.: parameter=binaryContentId, value=not-uuid, expectedType=UUID",
                          "details": {
                            "path": "/api/binaryContents/not-uuid",
                            "method": "GET"
                          },
                          "exceptionType": "MethodArgumentTypeMismatchException",
                          "status": 400,
                          "requestId": "7fdd2a75-dbeb-4e3a-8b86-98e33c61e0fa"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "파일을 찾을 수 없음",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "binaryContentNotFound",
                    description = "첨부 파일을 찾을 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-03T16:08:40.117674Z",
                          "code": "BINARY_CONTENT_NOT_FOUND",
                          "message": "파일을 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/binaryContents/e6681542-a6b1-43f3-b759-6dc9cfebcf41",
                            "method": "GET"
                          },
                          "exceptionType": "BinaryContentNotFoundException",
                          "status": 404,
                          "requestId": "1f485a6e-6293-4d5b-9cc1-5ec19969734e"
                        }
                        """
                )
            }
        )
    )
    BinaryContentDto find(UUID binaryContentId);

    @Operation(summary = "파일 다운로드")
    @Parameter(
        name = "binaryContentId",
        description = "다운로드할 파일 ID"
    )
    @ApiResponse(
        responseCode = "200",
        description = "파일 다운로드 성공",
        content = @Content(mediaType = "application/octet-stream")
    )
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "invalidParameterType",
                    description = "parameter(binaryContentId) 타입이 UUID가 아님",
                    value = """
                        {
                          "timestamp": "2025-09-03T16:07:55.585502Z",
                          "code": "INVALID_PARAMETER_VALUE",
                          "message": "요청 매개변수 값이 유효하지 않습니다.: parameter=binaryContentId, value=not-uuid, expectedType=UUID",
                          "details": {
                            "path": "/api/binaryContents/not-uuid/download",
                            "method": "GET"
                          },
                          "exceptionType": "MethodArgumentTypeMismatchException",
                          "status": 400,
                          "requestId": "7fdd2a75-dbeb-4e3a-8b86-98e33c61e0fa"
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "파일을 찾을 수 없음",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "binaryContentNotFound",
                    description = "첨부 파일을 찾을 수 없음",
                    value = """
                        {
                          "timestamp": "2025-09-03T16:08:40.117674Z",
                          "code": "BINARY_CONTENT_NOT_FOUND",
                          "message": "파일을 찾을 수 없습니다.",
                          "details": {
                            "path": "/api/binaryContents/e6681542-a6b1-43f3-b759-6dc9cfebcf41/download",
                            "method": "GET"
                          },
                          "exceptionType": "BinaryContentNotFoundException",
                          "status": 404,
                          "requestId": "1f485a6e-6293-4d5b-9cc1-5ec19969734e"
                        }
                        """
                )
            }
        )
    )
    ResponseEntity<Void> download(UUID binaryContentId);
}

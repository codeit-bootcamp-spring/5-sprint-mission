package com.sprint.mission.discodeit.presentation.api;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Tag(name = "User", description = "User API")
public interface UserApi {

    @Operation(summary = "User 등록")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "User 생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "중복된 email/username 사용",
                    content = @Content(examples = @ExampleObject(value = "User with email or username already exists"))
            )
    })
    ResponseEntity<UserResponse> create(
            @Parameter(
                    description = "생성할 User 요청 DTO",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ) CreateUserRequest request,
            @Parameter(
                    description = "User 프로필 이미지",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ) MultipartFile profileImage
    ) throws IOException;

    @Operation(summary = "전체 User 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "User 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))
            )
    })
    ResponseEntity<List<UserResponse>> findAll();

    @Operation(summary = "User 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "User 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(examples = @ExampleObject("User with email or username already exists"))
            )
    })
    ResponseEntity<UserResponse> update(
            @Parameter(
                    description = "수정할 User 정보 DTO",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ) UpdateUserRequest request,
            @Parameter(
                    description = "수정할 User 프로필 이미지",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ) MultipartFile profileImage
    ) throws IOException;

    @Operation(summary = "User 온라인 상태 업데이트")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "User 상태 변경 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
                    content=@Content(examples = @ExampleObject(value = "UserStatus with userId {userId} not found"))
            )
    })
    ResponseEntity<UserResponse> updateStatus(
            @Parameter(description = "상태를 변경할 User ID") UUID id
    );

    @Operation(summary = "User 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content=@Content(examples = @ExampleObject(value = "User with id {id} not found"))
            )
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "상태를 변경할 User ID") UUID id
    );
}

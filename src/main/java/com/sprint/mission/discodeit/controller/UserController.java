package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "사용자 API")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "사용자 생성", description = "멀티파트로 사용자 JSON과 프로필 이미지를 함께 업로드하여 생성합니다.", operationId = "createUser")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "생성 성공",
            content = {
                @Content(schema = @Schema(implementation = User.class)),
                @Content(mediaType = MediaType.ALL_VALUE)
            }),
        @ApiResponse(responseCode = "400", description = "요청 데이터 오류",
        content = @Content(mediaType = MediaType.ALL_VALUE)),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<User> create(
        @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest =
            Optional.ofNullable(profile).flatMap(this::resolveProfileRequest);
        User createdUser = userService.create(userCreateRequest, profileRequest);
//        URI location = URI.create("/api/users/" + createdUser.getId());
//        return ResponseEntity.created(location).body(createdUser);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdUser);
    }

    @PatchMapping(
        path = "/{userId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "사용자 수정", description = "지정한 사용자 ID의 정보를 수정합니다. 멀티파트(JSON+파일) 전송 지원.", operationId = "updateUser")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공",
            content = {
                @Content(schema = @Schema(implementation = User.class)),
                @Content(mediaType = MediaType.ALL_VALUE)
            }),
        @ApiResponse(responseCode = "404", description = "대상 사용자를 찾을 수 없음",
        content = @Content(mediaType = MediaType.ALL_VALUE)),
        @ApiResponse(responseCode = "400", description = "요청 오류",
        content = @Content(mediaType = MediaType.ALL_VALUE))
    })
    public ResponseEntity<User> update(
        @PathVariable("userId") UUID userId,
        @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest =
            Optional.ofNullable(profile).flatMap(this::resolveProfileRequest);
        User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping(path = "/{userId}")
    @Operation(summary = "사용자 삭제", description = "지정한 사용자 ID의 리소스를 삭제합니다.", operationId = "deleteUser")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공(본문 없음)"),
        @ApiResponse(responseCode = "404", description = "대상 사용자를 찾을 수 없음",
        content = @Content(mediaType = MediaType.ALL_VALUE))
    })
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "사용자 목록 조회", description = "전체 사용자 목록을 조회합니다.", operationId = "findAllUsers")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = {
                @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))),
                @Content(mediaType = MediaType.ALL_VALUE)
    })
    })
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PatchMapping(
        path = "/{userId}/userStatus",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "사용자 상태 변경", description = "지정한 사용자 ID의 상태만 부분적으로 변경합니다.", operationId = "updateUserStatusByUserId")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상태 변경 성공",
            content = {
                @Content(schema = @Schema(implementation = UserStatus.class)),
                @Content(mediaType = MediaType.ALL_VALUE)
            }),
        @ApiResponse(responseCode = "404", description = "대상 사용자를 찾을 수 없음",
        content = @Content(mediaType = MediaType.ALL_VALUE))
    })
    public ResponseEntity<UserStatus> updateUserStatusByUserId(
        @PathVariable("userId") UUID userId,
        @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
        return ResponseEntity.ok(updatedUserStatus);
    }

    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
        if (profileFile == null || profileFile.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(new BinaryContentCreateRequest(
                profileFile.getOriginalFilename(),
                profileFile.getContentType(),
                profileFile.getBytes()
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

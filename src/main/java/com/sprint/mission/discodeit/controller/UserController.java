package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
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
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @Operation(summary = "User 등록")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "User가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(examples = @ExampleObject(value = "User with email {email} already exists"))
            )
    })
    public ResponseEntity<UserDto> create(@RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
                                          @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {
        Optional<BinaryContentCreateRequest> contentCreateRequest = Optional.empty();

        if (profile != null && !profile.isEmpty()) {
            contentCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }
        UserDto user = userService.create(userCreateRequest, contentCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "User 정보 수정")
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "User 정보가 성공적으로 수행됨",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject("user with email {email} already exists"))
            )
    })
    public ResponseEntity<UserDto> update(
            @PathVariable("userId") UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {
        Optional<BinaryContentCreateRequest> contentCreateRequest = Optional.empty();

        if (profile != null && !profile.isEmpty()) {
            contentCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }

        UserDto updatedUser = userService.update(userId, userUpdateRequest, contentCreateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @Operation(summary = "User 삭제")
    @DeleteMapping(value = "/{userId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "User가 성공적으로 삭제됨"
            ),
            @ApiResponse(
                    responseCode = "404", description = "User를 찾을 수 없음"
            )
    })
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "전체 User 목록 조회")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "User 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
            )
    })
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }


    @Operation(summary = "User 온라인 상태 업데이트")
    @PatchMapping(value = "/{userId}/userStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "UserStatus with userId {userId} not found"))
            )
    })
    public ResponseEntity<UserStatusDto> updateStatusUser(
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatusDto userStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userStatus);
    }
}

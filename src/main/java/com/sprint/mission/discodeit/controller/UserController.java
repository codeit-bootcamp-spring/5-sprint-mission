package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor // final 필드를 매개변수로 받는 생성자를 자동 생성 (생성자 주입)
@RestController // REST API 컨트롤러
@RequestMapping("/api/users") // 모든 엔드포인트를 /api/users 하위 경로로 매핑
public class UserController implements UserApi { // UserApi 인터페이스 구현

    private final UserService userService;           // 사용자 CRUD 비즈니스 로직 처리
    private final UserStatusService userStatusService; // 사용자 상태(온라인/오프라인 등) 처리

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}) // POST /api/users (multipart/form-data 요청)
    @Override
    public ResponseEntity<UserDto> create(
        @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest, // JSON 부분 → 사용자 정보
        @RequestPart(value = "profile", required = false) MultipartFile profile // 파일 부분 → 프로필 이미지(선택)
    ) {
        // MultipartFile → BinaryContentCreateRequest 로 변환
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);

        // 서비스 호출 → 사용자 생성
        UserDto createdUser = userService.create(userCreateRequest, profileRequest);

        // 201 Created + 생성된 사용자 DTO 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @PatchMapping(
            path = "{userId}", // PATCH /api/users/{userId}
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE} // multipart/form-data 요청 허용
    )
    @Override
    public ResponseEntity<UserDto> update(
            @PathVariable("userId") UUID userId, // URL 경로 변수 userId 추출
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest, // JSON 부분 → 수정할 사용자 정보
            @RequestPart(value = "profile", required = false) MultipartFile profile // 파일 부분 → 새로운 프로필 이미지(선택)
    ) {
        // MultipartFile → BinaryContentCreateRequest 로 변환
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);

        // 서비스 호출 → 사용자 정보 수정
        UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);

        // 200 OK + 수정된 사용자 DTO 반환
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }

    @DeleteMapping(path = "{userId}") // DELETE /api/users/{userId}
    @Override
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
        userService.delete(userId); // 서비스 호출 → 사용자 삭제
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT) // 204 No Content
                .build(); // 바디 없음
    }

    @GetMapping // GET /api/users
    @Override
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll(); // 서비스 호출 → 모든 사용자 조회
        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(users); // 사용자 DTO 리스트 반환
    }

    @PatchMapping(path = "{userId}/userStatus") // PATCH /api/users/{userId}/userStatus
    @Override
    public ResponseEntity<UserStatusDto> updateUserStatusByUserId(@PathVariable("userId") UUID userId,
                                                                  @RequestBody UserStatusUpdateRequest request) {
        // 경로 변수 userId + 요청 바디(JSON)를 이용하여 사용자 상태 업데이트
        UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId, request);
        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(updatedUserStatus); // 수정된 사용자 상태 DTO 반환
    }

    // MultipartFile → BinaryContentCreateRequest 변환 메서드
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
        if (profileFile.isEmpty()) { // 빈 파일이면 Optional.empty() 반환
            return Optional.empty();
        } else {
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        profileFile.getOriginalFilename(), // 원본 파일명
                        profileFile.getContentType(),      // 파일 MIME 타입
                        profileFile.getBytes()             // 파일 내용 (byte[])
                );
                return Optional.of(binaryContentCreateRequest); // 변환 성공 시 Optional로 반환
            } catch (IOException e) {
                throw new RuntimeException(e); // 변환 실패 시 런타임 예외 발생
            }
        }
    }
}


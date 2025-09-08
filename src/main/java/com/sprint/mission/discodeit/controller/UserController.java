package com.sprint.mission.discodeit.controller; // 컨트롤러 패키지 선언

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
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

@RequiredArgsConstructor                                     // 생성자 주입(Lombok)
@RestController                                              // REST 컨트롤러 선언
@RequestMapping("/api/users")                                // 기본 URL 매핑
public class UserController implements UserApi {                                // 클래스 시작(메서드/라우트는 기존과 동일 유지)

    private final UserService userService;                   // 사용자 서비스 의존성
    private final UserStatusService userStatusService;       // 사용자 상태 서비스 의존성
    private final UserMapper userMapper;                     // User 엔티티→UserDto 매퍼 의존성
    private final UserStatusMapper userStatusMapper;         // UserStatus 엔티티→UserStatusDto 매퍼 의존성

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}) // 멀티파트 업로드(프로필 포함)
    public ResponseEntity<UserDto> create(                   // 반환 타입을 UserDto로 리팩토링
                                                             @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest, // JSON 파트
                                                             @RequestPart(value = "profile", required = false) MultipartFile profile // 파일 파트(선택)
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = // 프로필 파일 → 저장용 DTO 변환
                Optional.ofNullable(profile)                  // null 안전 처리
                        .flatMap(this::resolveProfileRequest); // 헬퍼로 변환
        User createdUser = userService.create(                // 서비스 호출로 생성
                userCreateRequest, profileRequest             // 요청/프로필 전달
        );                                                    // 엔티티 반환
        UserDto body = userMapper.toDto(createdUser);         // 엔티티를 DTO로 매핑
        return ResponseEntity                                 // 응답 빌더 시작
                .status(HttpStatus.CREATED)                   // 201 Created
                .body(body);                                  // DTO 본문 반환
    }

    @PatchMapping(                                           // 사용자 정보 수정
            path = "{userId}",                               // 경로 변수 포함
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE} // 멀티파트(프로필 교체 가능)
    )
    public ResponseEntity<UserDto> update(                   // 반환 타입을 UserDto로 리팩토링
                                                             @PathVariable("userId") UUID userId,             // 수정 대상 사용자 ID
                                                             @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest, // JSON 파트
                                                             @RequestPart(value = "profile", required = false) MultipartFile profile // 파일 파트(선택)
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = // 프로필 파일 → 저장용 DTO 변환
                Optional.ofNullable(profile)                  // null 안전 처리
                        .flatMap(this::resolveProfileRequest); // 헬퍼로 변환
        User updatedUser = userService.update(                // 서비스 호출로 수정
                userId, userUpdateRequest, profileRequest     // 인자 전달
        );                                                    // 엔티티 반환
        UserDto body = userMapper.toDto(updatedUser);         // 엔티티를 DTO로 매핑
        return ResponseEntity                                 // 응답 빌더 시작
                .status(HttpStatus.OK)                        // 200 OK
                .body(body);                                  // DTO 본문 반환
    }

    @DeleteMapping(path = "{userId}")                        // 사용자 삭제
    public ResponseEntity<Void> delete(                      // 본문 없는 응답
                                                             @PathVariable("userId") UUID userId              // 경로 변수: 사용자 ID
    ) {
        userService.delete(userId);                          // 서비스 호출로 삭제
        return ResponseEntity                                // 응답 빌더 시작
                .status(HttpStatus.NO_CONTENT)               // 204 No Content
                .build();                                    // 본문 없이 반환
    }

    @GetMapping                                             // 사용자 전체 조회
    public ResponseEntity<List<UserDto>> findAll() {        // 반환 타입은 기존과 동일(List<UserDto>)
        List<UserDto> users = userService.findAll();         // 서비스에서 DTO 목록 반환
        return ResponseEntity                                // 응답 빌더 시작
                .status(HttpStatus.OK)                       // 200 OK
                .body(users);                                // DTO 목록 본문 반환
    }

    @PatchMapping(path = "{userId}/userStatus")             // 사용자 상태 갱신
    public ResponseEntity<UserStatusDto> updateUserStatusByUserId( // 반환 타입을 UserStatusDto로 변경
                                                                   @PathVariable("userId") UUID userId,            // 대상 사용자 ID
                                                                   @RequestBody UserStatusUpdateRequest request    // 상태 업데이트 요청
    ) {
        UserStatus updatedUserStatus =                      // 서비스 호출로 상태 갱신
                userStatusService.updateByUserId(userId, request); // 엔티티 반환
        UserStatusDto body =                                // 엔티티를 DTO로 매핑
                userStatusMapper.toDto(updatedUserStatus);  // 매퍼 호출
        return ResponseEntity                               // 응답 빌더 시작
                .status(HttpStatus.OK)                      // 200 OK
                .body(body);                                // DTO 본문 반환
    }

    private Optional<BinaryContentCreateRequest> resolveProfileRequest( // 프로필 파일→저장 DTO 변환 헬퍼
                                                                        MultipartFile profileFile                                   // 업로드된 파일
    ) {
        if (profileFile == null || profileFile.isEmpty()) {             // 파일 없음/빈 파일이면
            return Optional.empty();                                     // 빈 Optional 반환
        } else {                                                         // 파일이 있으면
            try {                                                        // I/O 예외 처리
                BinaryContentCreateRequest binaryContentCreateRequest =  // 저장용 DTO 생성
                        new BinaryContentCreateRequest(                  // 생성자 호출
                                profileFile.getOriginalFilename(),       // 원본 파일명
                                profileFile.getContentType(),            // MIME 타입
                                profileFile.getBytes()                   // 파일 바이트
                        );                                               // 생성 끝
                return Optional.of(binaryContentCreateRequest);          // Optional로 감싸서 반환
            } catch (IOException e) {                                    // 예외 발생 시
                throw new RuntimeException(e);                           // 런타임 예외로 래핑
            }                                                            // catch 끝
        }                                                                // else 끝
    }                                                                    // 헬퍼 메서드 끝
}                                                                        // 클래스 끝

package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;


    @Override
    public User create(UserCreateRequest userCreateRequest, // 사용자가 입력한 기본 회원 정보 DTO를 가져옴
                       Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) { // 프로필 이미지 업로드 요청 DTO가 있을 수도 있고 없을 수도 있음
        String username = userCreateRequest.username(); // DTO에서 사용자 이름 가져옴
        String email = userCreateRequest.email(); // DTO에서 사용자 이메일 가져옴

        if (userRepository.existsByEmail(email)) { // 이메일이 이미 존재하면 예외 발생
            throw new IllegalArgumentException("User with email " + email + "already exists");
        }
        if (userRepository.existsByUsername(username)) { // 사용자명이 이미 존재하면 예외 발생
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }

        UUID nullableProfileId = optionalProfileCreateRequest // 프로필 이미지가 있으면 BinaryContent 엔티티를 만들어 저장하고, 저장된 ID를 반환.
                .map(profileRequest -> { // Optional이 값이 있으면 변환 로직 실행.
                    String fileName = profileRequest.fileName(); // 파일이름 저장
                    String contentType = profileRequest.contentType(); // ContentType 저장
                    byte[] bytes = profileRequest.bytes(); // 파일 데이터 저장
                    BinaryContent binaryContent = new BinaryContent(fileName, (long)bytes.length, contentType, bytes); // 파일 크기 (bytes.length를 long으로 캐스팅
                    return binaryContentRepository.save(binaryContent).getId(); // 저장된 ID를 반환
                })
                .orElse(null); // 이미지가 없으면 null 반환.
        String password = userCreateRequest.password(); // 비밀번호 꺼냄

        User user = new User(username, email, password, nullableProfileId); // User 엔티티 생성 후 저장.
        User createdUser = userRepository.save(user); // 저장된 객체(createdUser)를 받음 → DB가 자동 생성한 ID 포함.

        Instant now = Instant.now(); // 현재 시각(Instant.now())을 가져옴.
        UserStatus userStatus = new UserStatus(createdUser.getId(), now); // 회원의 상태(UserStatus)를 “현재 시각” 기준으로 저장.
        userStatusRepository.save(userStatus); // UserStatus 엔티티로 “마지막 활동 시간” 같은 상태로 저장

        return createdUser; // 최종적으로 생성된 User 엔티티를 반환.
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId) // UUID로 User 엔티티를 찾음. 반환 타입은 Optional<User>
                .map(this::toDto) // 값이 있으면 User -> UserDto로 변환. (User엔티티를 DTO로 바꿈, 밑에 구현함)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found")); // 값이 없으면 예외 던짐
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll() // 전체 사용자 엔티티 목록을 가져옴. 반환 타입은 List<User>
                .stream() // 스트림으로 변환
                .map(this::toDto) // User -> UserDto로 변환. (User엔티티를 DTO로 바꿈, 밑에 구현함)
                .toList(); // 변환된 요소들을 리스트로 모아 반환.
    }


    @Override
    public User update(UUID userId, UserUpdateRequest userUpdateRequest,
                       Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(userId) // userId로 사용자 엔티티를 찾음
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found")); // 없으면 예외 발생

        String newUsername = userUpdateRequest.newUsername(); // DTO에서 사용자 이름 가져옴
        String newEmail = userUpdateRequest.newEmail(); // DTO에서 사용자 이메일 가져옴
        if (userRepository.existsByEmail(newEmail)) {  // 이메일이 이미 존재하면 예외 발생
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
        if (userRepository.existsByUsername(newUsername)) { // 사용자명이 이미 존재하면 예외 발생
            throw new IllegalArgumentException("User with username " + newUsername + " already exists");
        }

        UUID nullableProfileId = optionalProfileCreateRequest
                .map(profileRequest -> {
                    Optional.ofNullable(user.getProfileId()) // Optional<BinaryContentCreateRequest>를 이용해 프로필 이미지 변경 요청이 있는 경우만 실행.
                            .ifPresent(binaryContentRepository::deleteById); // 기존 profileId가 있으면 binaryContentRepository.deleteById()로 삭제.

                    String fileName = profileRequest.fileName(); // 새 파일의 이름, 타입, 내용(byte[])을 꺼내 BinaryContent 객체 생성.
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes); // BinaryContent 객체 생성.
                    return binaryContentRepository.save(binaryContent).getId(); // 저장 후 새 프로필 ID(UUID) 반환.
                })
                .orElse(null); // 요청이 없으면 null.

        String newPassword = userUpdateRequest.newPassword(); // DTO에서 새로운 비밀번호를 받아 newPassword로 담고 적용함. 이게 밑에 객체 선언값으로 들어감
        user.update(newUsername, newEmail, newPassword, nullableProfileId); // 엔티티의 update() 메서드를 호출해 변경사항 적용.

        return userRepository.save(user); // 변경된 엔티티를 저장후 User 객체 반환
    }


    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId) // userId로 사용자 조회
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found")); // 사용자 없으면 예외 발생

        Optional.ofNullable(user.getProfileId()) // null이 아니면(즉, 프로필 이미지가 존재하면)
                .ifPresent(binaryContentRepository::deleteById); // 해당 이미지 삭제.
        userStatusRepository.deleteByUserId(userId); // UserStatus 테이블에서 해당 userId에 해당하는 상태(온라인 여부, 마지막 접속 시간 등) 삭제.

        userRepository.deleteById(userId); // 최종적으로 User 엔티티 삭제. 반환값 X
    }

    private UserDto toDto(User user) {
        Boolean online = userStatusRepository.findByUserId(user.getId()) //해당 사용자의 상태(UserStatus)를 찾음. 반환 타입이 Optional<UserStatus>
                .map(UserStatus::isOnline) // 값이 있으면 UserStatus 객체의 isOnline() 메서드 실행 -> Boolean 값으로 변환.
                .orElse(null); // 값이 없으면 null 반환

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online
        );
    }
}

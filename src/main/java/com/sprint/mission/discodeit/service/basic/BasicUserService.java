package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;                           // 응답 DTO
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;     // 프로필 업로드 DTO
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;              // 사용자 생성 DTO
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;              // 사용자 수정 DTO
import com.sprint.mission.discodeit.entity.BinaryContent;                       // 바이너리 엔티티
import com.sprint.mission.discodeit.entity.User;                                // 사용자 엔티티
import com.sprint.mission.discodeit.entity.UserStatus;                          // 상태 엔티티
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;         // 바이너리 레포지토리
import com.sprint.mission.discodeit.repository.UserRepository;                  // 사용자 레포지토리
import com.sprint.mission.discodeit.repository.UserStatusRepository;            // 상태 레포지토리
import com.sprint.mission.discodeit.service.UserService;                        // 서비스 인터페이스
import jakarta.transaction.Transactional;                                       // 트랜잭션
import lombok.RequiredArgsConstructor;                                          // 생성자 주입
import org.springframework.stereotype.Service;                                  // 스테레오타입

import java.time.Instant;                                                       // 시간
import java.util.List;                                                          // 리스트
import java.util.NoSuchElementException;                                        // 예외
import java.util.Optional;                                                      // 선택형
import java.util.UUID;                                                          // 식별자

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;                                 // User JPA 레포지토리
    private final BinaryContentRepository binaryContentRepository;               // BinaryContent JPA 레포지토리
    private final UserStatusRepository userStatusRepository;                     // UserStatus JPA 레포지토리
    private final BinaryContentMapper binaryContentMapper; // ★ 추가

    @Override
    @Transactional                                                               // 생성 로직 전체 원자성 보장
    public User create(                                                          // 사용자 생성
        UserCreateRequest userCreateRequest,                                 // 기본 정보 DTO
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest    // 선택 프로필 DTO
    ) {
        String username = userCreateRequest.username();                          // 사용자명 추출
        String email = userCreateRequest.email();                                 // 이메일 추출
        String password = userCreateRequest.password();                           // 비밀번호 추출(해시 전제)

        // --- 중복 검증 ---
        if (userRepository.existsByEmail(email)) {                                // 이메일 중복?
            throw new IllegalArgumentException("User with email " + email + " already exists"); // 예외
        }
        if (userRepository.existsByUsername(username)) {                          // 사용자명 중복?
            throw new IllegalArgumentException("User with username " + username + " already exists"); // 예외
        }

        // --- 사용자 엔티티 생성 ---
        User user = new User(username, email, password);                          // 필수 필드로 생성

        // --- 프로필(선택) 생성 및 부착 ---
        optionalProfileCreateRequest.ifPresent(req -> {                           // 프로필 업로드가 있다면
            String fileName = req.fileName();                                     // 파일명
            String contentType = req.contentType();                               // 콘텐츠 타입
            byte[] bytes = req.bytes();                                           // 바이트 배열
            BinaryContent profile = new BinaryContent(                             // BinaryContent 생성
                    fileName, (long) bytes.length, contentType//, bytes
            );
            BinaryContent saved = binaryContentRepository.save(profile);          // 저장
            user.changeProfile(saved);                                            // 사용자에 부착(1:1)
        });

        // --- 상태 엔티티 생성 및 양방향 연관관계 설정 ---
        User savedUser = userRepository.save(user);                               // 먼저 User 저장(id 필요)
        UserStatus status = new UserStatus(Instant.now());                        // 현재 시각으로 상태 생성
        savedUser.attachStatus(status);                                           // 양방향 세팅(user<->status)
        userStatusRepository.save(status);                                        // 상태 저장(FK: user_id)

        return savedUser;                                                         // 생성된 사용자 반환
    }

    @Override
    @Transactional                                                               // 지연 로딩 고려(읽기 트랜잭션)
    public UserDto find(UUID userId) {                                           // 사용자 단건 조회
        return userRepository.findById(userId)                                   // ID로 조회
                .map(this::toDto)                                                // 있으면 DTO 변환
                .orElseThrow(() -> new NoSuchElementException(                   // 없으면 예외
                        "User with id " + userId + " not found"
                ));
    }

    @Override
    @Transactional                                                               // 읽기 트랜잭션
    public List<UserDto> findAll() {                                             // 전체 사용자 조회
        return userRepository.findAll()                                          // 전부 가져오기
                .stream()                                                        // 스트림
                .map(this::toDto)                                                // DTO 변환
                .toList();                                                       // 리스트로 수집
    }

    @Override
    @Transactional                                                               // 업데이트 트랜잭션
    public User update(                                                          // 사용자 수정
       UUID userId,                                                         // 대상 ID
       UserUpdateRequest userUpdateRequest,                                 // 변경 DTO
       Optional<BinaryContentCreateRequest> optionalProfileCreateRequest    // 프로필 교체(선택)
    ) {
        User user = userRepository.findById(userId)                              // 먼저 엔티티 로딩
                .orElseThrow(() -> new NoSuchElementException(                   // 없으면 예외
                        "User with id " + userId + " not found"
                ));

        String newUsername = userUpdateRequest.newUsername();                    // 새 사용자명
        String newEmail = userUpdateRequest.newEmail();                          // 새 이메일
        String newPassword = userUpdateRequest.newPassword();                    // 새 비밀번호

        // --- 중복 검증(자기 자신 제외) ---
        if (newEmail != null && !newEmail.equals(user.getEmail())                // 이메일 변경 의도 &&
                && userRepository.existsByEmail(newEmail)) {                     // 이미 존재?
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
        if (newUsername != null && !newUsername.equals(user.getUsername())       // 사용자명 변경 의도 &&
                && userRepository.existsByUsername(newUsername)) {               // 이미 존재?
            throw new IllegalArgumentException("User with username " + newUsername + " already exists");
        }

        // --- 기본 정보 업데이트 ---
        user.update(newUsername, newEmail, newPassword);                         // 필요한 필드만 변경

        // --- 프로필 교체 처리(옵션) ---
        if (optionalProfileCreateRequest.isPresent()) {                          // 교체 요청이 있으면
            // 기존 프로필이 있었다면 JPA orphanRemoval로 자동 제거됨(교체 시 대입만 하면 됨)
            BinaryContentCreateRequest req = optionalProfileCreateRequest.get(); // 요청 꺼내기
            BinaryContent newProfile = new BinaryContent(                        // 새 프로필 생성
                    req.fileName(),
                    (long) req.bytes().length,
                    req.contentType()
                    //req.bytes()
            );
            BinaryContent saved = binaryContentRepository.save(newProfile);      // 저장
            user.changeProfile(saved);                                           // 교체(대입)
        }

        return userRepository.save(user);                                        // 변경 사항 저장 및 반환
    }

    @Override
    @Transactional                                                               // 삭제 트랜잭션
    public void delete(UUID userId) {                                            // 사용자 삭제
        User user = userRepository.findById(userId)                              // 존재 확인
                .orElseThrow(() -> new NoSuchElementException(                   // 없으면 예외
                        "User with id " + userId + " not found"
                ));

        // 프로필은 orphanRemoval=true 이므로 사용자에서 연관 제거/삭제 시 자동 정리
        // 상태(UserStatus) 역시 orphanRemoval=true(부모: User)로 함께 삭제됨
        userRepository.delete(user);                                             // 최종 삭제
    }

    // --- 엔티티 -> DTO 변환 ---
    private UserDto toDto(User user) {
        Boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(null);

        // ★ 변경: BinaryContent -> BinaryContentDto
        BinaryContentDto profileDto = null;
        BinaryContent profile = user.getProfile();
        if (profile != null) {
            profileDto = binaryContentMapper.toDto(profile); // bytes는 null로 매핑되는 구현이면 그대로 사용
        }

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                profileDto,   // ★ 여기 이제 BinaryContentDto
                online
        );
    }

}

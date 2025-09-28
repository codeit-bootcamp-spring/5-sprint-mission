package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor // Lombok: final 필드를 매개변수로 받는 생성자를 자동 생성
@Service // 스프링 서비스 컴포넌트로 등록
public class BasicUserService implements UserService { // 사용자 도메인 비즈니스 로직을 담당하는 서비스 구현체

    private final UserRepository userRepository; // User 엔티티 저장/조회 리포지토리
    private final UserStatusRepository userStatusRepository; // UserStatus 엔티티 저장/조회 리포지토리
    private final UserMapper userMapper; // User ↔ UserDto 변환 매퍼
    private final BinaryContentRepository binaryContentRepository; // 프로필 이미지 등 바이너리 메타데이터 저장 리포지토리
    private final BinaryContentStorage binaryContentStorage; // 실제 파일 바이트를 저장/조회하는 스토리지

    @Transactional // 생성 과정 전체를 하나의 트랜잭션으로 처리
    @Override // 인터페이스 메서드 구현
    public UserDto create(UserCreateRequest userCreateRequest, // 사용자 생성 요청 DTO
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) { // 선택적 프로필 이미지 업로드 요청
        String username = userCreateRequest.username(); // 요청에서 username 추출
        String email = userCreateRequest.email(); // 요청에서 email 추출

        if (userRepository.existsByEmail(email)) { // 이메일 중복 여부 확인
            throw new IllegalArgumentException("User with email " + email + " already exists"); // 중복 시 예외
        }
        if (userRepository.existsByUsername(username)) { // username 중복 여부 확인
            throw new IllegalArgumentException("User with username " + username + " already exists"); // 중복 시 예외
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest // 프로필 업로드 요청이 있을 경우
                .map(profileRequest -> { // BinaryContent 엔티티로 변환
                    String fileName = profileRequest.fileName(); // 파일명
                    String contentType = profileRequest.contentType(); // MIME 타입
                    byte[] bytes = profileRequest.bytes(); // 파일 바이트
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, // 메타데이터 엔티티 생성
                            contentType);
                    binaryContentRepository.save(binaryContent); // 메타데이터 DB 저장
                    binaryContentStorage.put(binaryContent.getId(), bytes); // 바이트를 스토리지에 저장 (키: BinaryContent ID)
                    return binaryContent; // 생성된 BinaryContent 반환
                })
                .orElse(null); // 업로드가 없으면 null

        String password = userCreateRequest.password(); // 요청에서 password 추출

        User user = new User(username, email, password, nullableProfile); // User 엔티티 생성(프로필은 null 가능)
        Instant now = Instant.now(); // 현재 시각
        UserStatus userStatus = new UserStatus(user, now); // 사용자 상태 엔티티 생성(예: 가입/온라인 기준 시각)
        // 참고: userStatusRepository.save(...) 호출은 코드에 없음(지연 저장이나 cascade 설정 여부에 따라 달라질 수 있음)

        userRepository.save(user); // User 엔티티 저장
        return userMapper.toDto(user); // 저장된 User를 DTO로 변환하여 반환
    }

    @Override // 인터페이스 메서드 구현
    public UserDto find(UUID userId) { // 사용자 단건 조회
        return userRepository.findById(userId) // ID로 조회
                .map(userMapper::toDto) // 존재하면 DTO로 매핑
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found")); // 없으면 예외
    }

    @Override // 인터페이스 메서드 구현
    public List<UserDto> findAll() { // 사용자 전체 조회
        return userRepository.findAllWithProfileAndStatus() // 프로필/상태를 패치 조인으로 함께 조회
                .stream() // 스트림 처리
                .map(userMapper::toDto) // DTO 변환
                .toList(); // 리스트로 수집
    }

    @Transactional // 수정 작업을 트랜잭션으로 처리
    @Override // 인터페이스 메서드 구현
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest, // 사용자 정보 업데이트
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) { // 프로필 교체가 있을 수 있음
        User user = userRepository.findById(userId) // 대상 사용자 조회
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found")); // 없으면 예외

        String newUsername = userUpdateRequest.newUsername(); // 새 username
        String newEmail = userUpdateRequest.newEmail(); // 새 email
        if (userRepository.existsByEmail(newEmail)) { // 이메일 중복 검사
            throw new IllegalArgumentException("User with email " + newEmail + " already exists"); // 중복 시 예외
        }
        if (userRepository.existsByUsername(newUsername)) { // username 중복 검사
            throw new IllegalArgumentException("User with username " + newUsername + " already exists"); // 중복 시 예외
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest // 새 프로필 업로드 요청이 있을 경우
                .map(profileRequest -> { // BinaryContent 생성/저장
                    String fileName = profileRequest.fileName(); // 파일명
                    String contentType = profileRequest.contentType(); // MIME 타입
                    byte[] bytes = profileRequest.bytes(); // 파일 바이트
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, // 메타데이터 엔티티 생성
                            contentType);
                    binaryContentRepository.save(binaryContent); // 메타데이터 저장
                    binaryContentStorage.put(binaryContent.getId(), bytes); // 바이트 저장
                    return binaryContent; // 생성된 BinaryContent 반환
                })
                .orElse(null); // 업로드가 없으면 null

        String newPassword = userUpdateRequest.newPassword(); // 새 비밀번호
        user.update(newUsername, newEmail, newPassword, nullableProfile); // 엔티티 필드 업데이트(더티 체킹으로 flush 예정)

        return userMapper.toDto(user); // 업데이트된 User를 DTO로 변환하여 반환
    }

    @Transactional // 삭제 작업을 트랜잭션으로 처리
    @Override // 인터페이스 메서드 구현
    public void delete(UUID userId) { // 사용자 삭제
        if (userRepository.existsById(userId)) { // 현재 조건: 존재하면 예외를 던짐
            throw new NoSuchElementException("User with id " + userId + " not found"); // 존재하는 경우에도 예외 발생
        }
        // 위 조건은 로직상 "존재하지 않으면 예외" 패턴과는 다름(설계 의도에 따라 조건식이 달라질 수 있음)

        userRepository.deleteById(userId); // 사용자 삭제 실행
    }
}

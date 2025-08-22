package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Primary
@RequiredArgsConstructor // final 생성자 자동 생성(this 생략 가능)
public class FileUserService implements UserService {

  private final UserRepository repository; // 유저 기본 저장
  private final UserStatusRepository statusRepository; // 유저 상태 저장
  private final BinaryContentRepository binaryRepository; // 프로필 이미지 등 파일 데이터 저장


  @Override
  public User create(UserCreateRequest request, MultipartFile profile) {
    if (request == null) {
      throw new IllegalArgumentException("UserCreateRequest가 null입니다.");
    }

    // 🔒 username, email 중복 확인
    if (repository.existsByUsername(request.getUsername())) {
      throw new IllegalArgumentException("이미 존재하는 username입니다.");
    }
    if (repository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("이미 존재하는 email입니다.");
    }

    //User 엔티티로 변환
    User user = new User(request.getUsername(), request.getPassword(), request.getEmail());

    //1. Optional 프로필 이미지 처리(BinaryContent 연동시 추가)
    if (profile != null && !profile.isEmpty()) {
      BinaryContent image = new BinaryContent(
          UUID.randomUUID(),
          Instant.now(),
          user.getId(),
          profile.getOriginalFilename(),
          profile.getContentType(),
          profile.getSize()
          // 필요하면 profile.getBytes()도 추가!
      );
      binaryRepository.save(image);
      //프로필 이미지 ID를 User에 연결!
      user.setProfileId(image.getId());
    }

    //2. UserStatus 함께 생성
    UserStatus status = new UserStatus(
        UUID.randomUUID(),
        Instant.now(),
        user.getId(),
        Instant.now() // 생성 시점 = 마지막 접속 시점
    );
    statusRepository.save(status);

    //3. 저장
    repository.save(user);
    return user;
  }

  @Override
  public UserDto findById(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("조회할 유저 ID가 null입니다.");
    }

    User user = repository.findById(userId);
    if (user == null) {
      throw new IllegalArgumentException("존재하지 않는 ID입니다.");
    }

    // isOnline 판단
    UserStatus status = statusRepository.findByUserId(user.getUsername());
    boolean isOnline = false;
    Instant lastOnline = null;
    if (status != null) {
      lastOnline = status.getLastOnline();
      isOnline = lastOnline != null && Instant.now().minusSeconds(300).isBefore(lastOnline);
    }

    return new UserDto( //UserResponse 클래스 안 생성자의 파라미터 순서 및 타입과 정확히 일치해야 함
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        isOnline
    );
  }

  @Override
  public List<UserDto> findAll() {
    return repository.findAll().stream()
        .map(user -> {
          UserStatus status = statusRepository.findByUserId(user.getUsername());
          boolean isOnline = false;
          if (status != null && status.getLastOnline() != null) {
            isOnline = Instant.now().minusSeconds(300).isBefore(status.getLastOnline());
          }
          return UserDto.fromEntity(user, isOnline);
        })
        .toList();
  }


  @Override
  public User update(UUID userId, UserUpdateRequest request, MultipartFile profileImage)
      throws IOException {
    if (request == null) {
      throw new IllegalArgumentException("UserUpdateRequest가 null입니다.");
    }

    //1. 기존 유저 조회
    User user = repository.findById(userId);
    if (user == null) {
      throw new IllegalArgumentException("해당 ID를 가진 유저가 존재하지 않습니다.");
    }

    //2. 변경값 적용
    user.setUsername(request.getNewUsername());
    user.setEmail(request.getNewEmail());
    user.setPassword(request.getNewPassword());
    repository.update(user);

    //3. 프로필 이미지 대체 처리 (optional)
    if (profileImage != null && !profileImage.isEmpty()) {
      // 기존 이미지 삭제
      binaryRepository.deleteByOwnerId(user.getId());
      // 새 이미지 저장
      BinaryContent newImage = new BinaryContent(
          UUID.randomUUID(),
          Instant.now(),
          user.getId(),
          profileImage.getOriginalFilename(),
          profileImage.getContentType(),
          profileImage.getSize(),
          profileImage.getBytes()
      );
      binaryRepository.save(newImage);
    }

    //4. UserStatus 갱신 (lastOnline 시간 갱신)
    UserStatus status = statusRepository.findByUserId(user.getUsername());
    if (status != null) {
      status.setLastOnline(Instant.now());
      statusRepository.update(status);
    }

    return user;
  }


  @Override
  public void delete(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("삭제 유저가 null입니다.");
    }
    if (repository.findById(userId) == null) {
      throw new IllegalStateException("삭제할 유저가 존재하지 않습니다: " + userId);
    }

    // ✅ BinaryContent 함께 삭제
    binaryRepository.deleteByOwnerId(userId);

    // ✅ UserStatus 함께 삭제 (있으면)
    User user = repository.findById(userId);
    if (user != null) {
      UserStatus status = statusRepository.findByUserId(user.getUsername());
      if (status != null) {
        statusRepository.delete(status.getId());
      }
    }

    // ✅ 마지막으로 유저 삭제
    repository.delete(userId);
  }


  @Override
  public boolean existsByUsername(String username) {
    return repository.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

  @Override
  public Optional<User> findEntityById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    User user = repository.findById(id);
    return Optional.ofNullable(user);
  }

}

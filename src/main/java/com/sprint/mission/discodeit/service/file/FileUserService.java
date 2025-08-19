package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor // final 생성자 자동 생성(this 생략 가능)
public class FileUserService implements UserService {

    private final UserRepository repository; // 유저 기본 저장
    private final UserStatusRepository statusRepository; // 유저 상태 저장
    private final BinaryContentRepository binaryRepository; // 프로필 이미지 등 파일 데이터 저장


    @Override
    public User create(UserCreateRequest request, MultipartFile profileImage) {
        if (request == null)
            throw new IllegalArgumentException("UserCreateRequest가 null입니다.");

        // 🔒 username, email 중복 확인
        if (repository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 username입니다.");
        }
        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 email입니다.");
        }


        //User 엔티티로 변환
        User user = new User(request.getUserId(), request.getPassword(), request.getEmail());

        //1. Optional 프로필 이미지 처리(BinaryContent 연동시 추가)
        if (profileImage != null && !profileImage.isEmpty()) {
            BinaryContent image = new BinaryContent(
                    UUID.randomUUID(),
                    Instant.now(),
                    user.getId(),
                    profileImage.getOriginalFilename(),
                    profileImage.getContentType(),
                    profileImage.getSize()
                    // 필요하면 profileImage.getBytes()도 추가!
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
    public UserResponse findById(UUID id) {
        if (id == null) throw new IllegalArgumentException("조회할 유저 ID가 null입니다.");

        User user = repository.findById(id);
        if (user == null) throw new IllegalArgumentException("존재하지 않는 ID입니다.");

        // isOnline 판단
        UserStatus status = statusRepository.findByUserId(user.getUserId());
        boolean isOnline = false;
        Instant lastOnline = null;
        if (status != null) {
            lastOnline = status.getLastOnline();
            isOnline = lastOnline != null && Instant.now().minusSeconds(300).isBefore(lastOnline);
        }

        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getProfileId(),
                user.getUserId(),
                user.getEmail(),
                isOnline,
                lastOnline
        );
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> users = repository.findAll();

        return users.stream().map(user -> {
            UserStatus status = statusRepository.findByUserId(user.getUserId());
            boolean isOnline = false;
            Instant lastOnline = null;
            if (status != null) {
                lastOnline = status.getLastOnline();
                isOnline = lastOnline != null && Instant.now().minusSeconds(300).isBefore(lastOnline);
            }

            return new UserResponse(
                    user.getId(),            // 1
                    user.getCreatedAt(),     // 2
                    user.getUpdatedAt(),     // 3
                    user.getProfileId(),     // 4
                    user.getUserId(),        // 5
                    user.getEmail(),         // 6
                    isOnline,                // 7
                    lastOnline               // 8
            );
        }).toList();
    }


    @Override
    public User update(UserUpdateRequest request, MultipartFile profileImage) throws IOException {
        if (request == null) {
            throw new IllegalArgumentException("UserUpdateRequest가 null입니다.");
        }

        //1. 기존 유저 조회
        User user = repository.findById(request.getId());
        if (user == null) {
            throw new IllegalArgumentException("해당 ID를 가진 유저가 존재하지 않습니다.");
        }

        //2. 변경값 적용
        user.setUserId(request.getUserId());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
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
        UserStatus status = statusRepository.findByUserId(user.getUserId());
        if (status != null) {
            status.setLastOnline(Instant.now());
            statusRepository.update(status);
        }

        return user;
    }


    @Override
    public void delete(UUID id) {
        // 🔒 ID가 null이면 예외
        if (id == null) {
            throw new IllegalArgumentException("삭제할 유저 ID가 null입니다.");
        }
        if (repository.findById(id) == null) {
            throw new IllegalStateException("삭제할 유저가 존재하지 않습니다: " + id);
        }

        // ✅ BinaryContent 함께 삭제
        binaryRepository.deleteByOwnerId(id);

        // ✅ UserStatus 함께 삭제 (있으면)
        User user = repository.findById(id);
        if (user != null) {
            UserStatus status = statusRepository.findByUserId(user.getUserId());
            if (status != null) {
                statusRepository.delete(status.getId());
            }
        }

        // ✅ 마지막으로 유저 삭제
        repository.delete(id);
    }


    @Override
    public boolean existsByUsername(String userId) {
        return repository.existsByUserId(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Optional<User> findEntityById(UUID id) {
        if (id == null) return Optional.empty();
        User user = repository.findById(id);
        return Optional.ofNullable(user);
    }

}

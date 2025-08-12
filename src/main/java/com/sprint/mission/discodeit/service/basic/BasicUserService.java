package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.ProfileImageRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("basicUserService")     // Sping Context에 Bean으로 등록
@RequiredArgsConstructor         // 생성자 주입
public class BasicUserService implements UserService {

    private final UserRepository repository;

    @Override
    public void create(UserCreateRequest request) {
        if (request == null) throw new IllegalArgumentException("UserCreateRequest가 null입니다.");
        if (repository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 username입니다.");
        }

        //DTO -> Entity 변환
        User user = new User(request.getUserId(), request.getEmail(), request.getPassword());

        //ProfileImage 처리 (나중에 BinaryContent 연동시 추가)
        ProfileImageRequest profile = request.getProfileImage();
        if (profile != null) {
            // 저장 및 연결
        }
        repository.save(user);
    }

    @Override
    public UserResponse findById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("조회할 유저 ID가 null입니다.");
        User user = repository.findById(id);
        if (user == null) throw new IllegalArgumentException("존재하지 않는 ID입니다.");

        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUserId(),
                user.getEmail(),
                false, // isOnline → 나중에 UserStatus로 설정할 수 있어
                null   // lastOnline → 나중에 UserStatus에서 가져오기
        );
    }

    @Override
    public List<UserResponse> findAll() {
        return repository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getUserId(),
                        user.getEmail(),
                        false,
                        null
                ))
                .collect(Collectors.toList());
    }

    // ✅ UPDATE
    @Override
    public void update(UserUpdateRequest request) {
        if (request == null || request.getId() == null) {
            throw new IllegalArgumentException("업데이트할 정보가 올바르지 않습니다.");
        }

        User user = repository.findById(request.getId());
        if (user == null) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
        }

        user.setUserId(request.getUserId());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        if (request.getProfileImage() != null) {
        }

        repository.update(user);
    }

    @Override
    public void delete(UUID id) {
        if (id == null) throw new IllegalArgumentException("삭제할 유저 ID가 null입니다.");
        if (repository.findById(id) == null) {
            throw new IllegalStateException("삭제할 유저가 존재하지 않습니다: " + id);
        }
        repository.delete(id);
    }
}

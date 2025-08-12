package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor // final 생성자 자동 생성(this 생략 가능)
public class FileUserService implements UserService {

    private final UserRepository repository; // 생성자 주입

    @Override
    public void create(UserCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("UserCreateRequest가 null입니다.");
        }
        if (repository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 username입니다.");
        }

        //User 엔티티로 변환
        User user = new User(request.getUserId(), request.getPassword(), request.getEmail());

        //1. Optional 프로필 이미지 처리(BinaryContent 연동시 추가)
        if (request.getProfileImage() != null) {
            //BinaryContent 저장
        }

        //2. UserStatus도 필요

        repository.save(user);
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = repository.findById(id);
        if (id == null) {
            throw new IllegalArgumentException("조회할 유저 ID가 null입니다.");
        }
        User original = repository.findById(id);
        if (original == null) {
            throw new IllegalArgumentException("존재하지 않는 ID입니다.");
        }

        // User → UserResponse 변환
        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUserId(),
                user.getEmail(),
                false, // isOnline 추후 UserStatus 기반으로 계산
                null   // lastOnline도 UserStatus 기반 처리 예정
        );
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> users = repository.findAll(); // 엔티티 전부 가져오기

        // User 엔티티에서 UserResponse로 변환
        return users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getUserId(),
                        user.getEmail(),
                        false,
                        null
                ))
                .toList(); // 변환된 List<UserResponse> 반환
    }

    @Override
    public void update(UserUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("UserUpdateRequest가 null입니다.");
        }
        if (repository.findById(request.getId()) == null) {
            throw new IllegalArgumentException("해당 ID를 가진 유저가 존재하지 않습니다.");
        }

        //1. 기존 유저 조회
        User user = repository.findById(request.getId());

        //변경값 적용
        user.setUserId(request.getUserId());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        repository.update(user);
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
        repository.delete(id);
    }
}

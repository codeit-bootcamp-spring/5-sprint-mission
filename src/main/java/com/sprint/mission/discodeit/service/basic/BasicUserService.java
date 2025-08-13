package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentService binaryContentService;
    private final UserStatusService userStatusService;

    @Override
    public UserResponse.detail create(UserRequest.create dto) {

        // email 중복검사
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // user 도메인 생성 및 저장
        User user = new User(dto.name(), dto.email(), dto.password());
        userRepository.save(user);

        // user 상태 생성 및 저장
        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);

        // 프로필 이미지가 있을 경우
        if (dto.profileImage() != null && !dto.profileImage().isEmpty()) {
                UUID profileImageId =  binaryContentService.save(dto.profileImage());
                user.updateProfileId(profileImageId);
        }

        UUID imageId = user.getProfileId();
        String imageUrl = (imageId != null) ? "/binary/" + imageId : null;

        return UserResponse.detail.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .imageId(imageId)
                .imageUrl(imageUrl)
                .createdAt(user.getCreatedAtFormatted())
                .updatedAt(user.getUpdatedAtFormatted())
                .online(userStatus.isOnline())
                .build();
    }

    @Override
    public User update(UserRequest.update dto) {
        // 사용자 조회
        User user = userRepository.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 이름 수정
        if (dto.name() != null) {
            user.updateName(dto.name());
        }

        // 프로필 이미지가 있을 경우
        if (dto.profileImage() != null && !dto.profileImage().isEmpty()) {
                UUID profileImageId =  binaryContentService.save(dto.profileImage());
                user.updateProfileId(profileImageId);
        }

        // 변경된 user 저장
        userRepository.save(user);
        return user;
    }

    @Override
    public List<UserResponse.summary> findAll() {

        return userRepository.findAll().stream()
                .map(u -> UserResponse.summary.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .imageId(u.getProfileId())
                        .imageUrl(u.getProfileId() != null ? "/binary/" + u.getProfileId() : null)
                        .online(userStatusService.isOnline(u.getId()))
                        .build()
                ).toList();
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 사용자가 존재하지 않습니다."));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 존재하지 않습니다."));
    }

    @Override
    public User updatePassword(UserRequest.passwordReset req) {

        // 사용자 조회
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 사용자가 존재하지 않습니다."));

        // 현재 비밀번호 검증
        if(!user.getPassword().equals(req.password())){
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 변경 및 저장
        user.updatePassword(req.newPassword());
        return userRepository.save(user);
    }

    @Override
    public boolean delete(UUID id) {
        return userRepository.delete(id);
    }
}

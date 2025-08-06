package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.JoinDto;
import com.sprint.mission.discodeit.dto.user.UpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    private final UserStatusRepository userStatusRepository;

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User create(JoinDto dto) {
        // 이메일 중복 검사
        if(userRepository.findByEmail(dto.email())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // username 중복 검사
        if(userRepository.findByUsername(dto.username()) != null) {
            throw new IllegalArgumentException("Username is already in use");
        }

        // 사용자 저장
        User user = new User(dto.username(), dto.email(), dto.password());

        // 사용자 상태 저장
        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);

        // 프로필 이미지
        if(dto.profileImage() != null) {
            binaryContentRepository.save(dto.profileImage());
            user.updateProfileId(dto.profileImage().getId());
        }

        return user;
    }

    @Override
    public User find(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UpdateDto dto) {
        User user = userRepository.findById(dto.id())
                .orElseThrow(() -> new NoSuchElementException("User with id " + dto.id() + " not found"));

        if(dto.name() != null) {
            user.updateName(dto.name());
        }

        if(dto.email() != null) {
            user.updateEmail(dto.email());
        }

        if(dto.profileImage() != null) {
            binaryContentRepository.save(dto.profileImage());
            user.updateProfileId(dto.profileImage().getId());
        }

        userRepository.save(user);
        return user;
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
        binaryContentRepository.deleteById(userId);
        userStatusRepository.deleteById(userId);
    }
}

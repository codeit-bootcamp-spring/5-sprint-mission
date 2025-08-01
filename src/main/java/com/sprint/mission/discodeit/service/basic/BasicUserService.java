package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.respository.BinaryContentRepository;
import com.sprint.mission.discodeit.respository.UserRepository;
import com.sprint.mission.discodeit.respository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserStatusService userStatusService;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User create(UserDto.Create dto) {
        // name 중복 검사
        userRepository.findByName(dto.name()).ifPresent(user -> {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        });
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
        if(dto.profileImage() != null){
            BinaryContent image = new BinaryContent(dto.profileImage());
            binaryContentRepository.save(image);
        }

        return user;
    }

    @Override
    public User update(UserDto.Update dto) {
        // 사용자 조회
        User user = userRepository.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 이름 수정
        if (dto.name() != null) {
            // name 중복 검사
            userRepository.findByName(dto.name()).ifPresent(u -> {
                throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
            });
            user.updateName(dto.name());
        }

        // email 수정
        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            userRepository.findByEmail(dto.email()).ifPresent(duplicate -> {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            });
            user.updateEmail(dto.email());
        }

        // 프로필 이미지 수정
        if (dto.profileImage() != null) {
            BinaryContent newImage = new BinaryContent(dto.profileImage());
            binaryContentRepository.save(newImage);
        }

        // 변경된 user 저장
        userRepository.save(user);

        return user;
    }

    @Override
    public List<UserDto.View> findAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto.View(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        userStatusService.isOnline(user.getId())
                ))
                .toList();
    }

    @Override
    public Optional<UserDto.View> findById(UUID id) {
        return userRepository.findById(id)
                .map(user -> new UserDto.View(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        userStatusService.isOnline(user.getId())
                ));
    }

    @Override
    public Optional<UserDto.View> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UserDto.View(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        userStatusService.isOnline(user.getId())
                ));
    }


    @Override
    public boolean delete(UUID id) {
        return userRepository.delete(id);
    }
}

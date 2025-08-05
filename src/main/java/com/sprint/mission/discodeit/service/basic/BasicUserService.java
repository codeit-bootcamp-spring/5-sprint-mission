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
            BinaryContent image = BinaryContent.from(dto.profileImage());
            binaryContentRepository.save(image);
            user.updateProfileId(image.getId());
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
            user.updateName(dto.name());
        }

        // 프로필 이미지 수정
        if (dto.profileImage() != null) {
            BinaryContent newImage = BinaryContent.from(dto.profileImage());
            binaryContentRepository.save(newImage);
            user.updateProfileId(newImage.getId());
        }

        // 변경된 user 저장
        userRepository.save(user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
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
    public User updatePassword(UUID userId, String currentPassword, String newPassword) {

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 사용자가 존재하지 않습니다."));

        // 현재 비밀번호 검증
        if(!user.getPassword().equals(currentPassword)){
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 유효성 검사
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("새 비밀번호는 비어 있을 수 없습니다.");
        }

        // 비밀번호 변경 및 저장
        user.updatePassword(newPassword);
        return userRepository.save(user);
    }

    @Override
    public boolean delete(UUID id) {
        return userRepository.delete(id);
    }
}

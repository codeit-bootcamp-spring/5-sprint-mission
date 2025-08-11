package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(UserDto.CreateUser createUser) {
        if (userRepository.findByUsername(createUser.username()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다: " + createUser.username());
        }
        if (userRepository.findByEmail(createUser.email()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + createUser.email());
        }

        User user = new User(createUser.username(), createUser.email(), createUser.password());
        return userRepository.save(user);
    }


    @Override
    public User update(UserDto.UpdateUser updateUser) {
        User user = userRepository.findById(updateUser.id())
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다"));

        user.update(updateUser.username(), updateUser.email(), updateUser.password());

        return userRepository.save(user);
    }


    @Override
    public User find(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("사용자를 찾을 수 없습니다");
        }
        userRepository.deleteById(userId);
    }
}

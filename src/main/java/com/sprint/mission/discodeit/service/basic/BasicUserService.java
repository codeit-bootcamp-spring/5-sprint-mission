package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService  implements UserService {
    private final UserRepository userRepository;

    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository

    @Override
    public void createUser(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest) {

    }

    @Override
    public User readByIdUser(UUID name) {
        return null;
    }

    @Override
    public void readAllUser() {

    }

    @Override
    public void updateUser(UUID user, String username, String password) {

    }

    @Override
    public void deleteByIdUser(UUID user) {

    }
}
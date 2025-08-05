package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final JCFUserRepository userRepository;

    public JCFUserService(JCFUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto.DetailResponse create(UserDto.CreateRequest request) {
        return null;
    }

    @Override
    public UserDto.DetailResponse update(UserDto.UpdateRequest request) {
        return null;
    }

    @Override
    public UserDto.DetailResponse findById(UUID id) {
        return null;
    }

    @Override
    public List<UserDto.DetailResponse> findAll() {
        return List.of();
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id).orElse(null);

        if (user != null) {
            userRepository.delete(id);
        }
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}

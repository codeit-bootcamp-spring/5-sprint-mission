package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final FileUserRepository userRepository;

    public FileUserService(FileUserRepository userRepository) {
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

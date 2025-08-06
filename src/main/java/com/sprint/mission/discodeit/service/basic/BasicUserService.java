package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AddUserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final ChannelService channelService;

    @Override
    public User addUser(AddUserDto addUserDto) {
        User user = new User(addUserDto.getUserName(), addUserDto.getEmail(), addUserDto.getPassword(), addUserDto.getPhoneNumber());
        Optional<User> addedUser = userRepository.save(user);
        return addedUser.orElseThrow();
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UUID userId, AddUserDto addUserDto) {
        User user = userRepository.findById(userId).orElseThrow();

        user.updateUserName(addUserDto.getUserName());
        user.updateEmail(addUserDto.getEmail());
        user.updatePassword(addUserDto.getPassword());
        user.updatePhoneNumber(addUserDto.getPhoneNumber());

        return userRepository.save(user).orElseThrow();
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.delete(userId);
    }

    @Override
    public void deleteAllUser() {
        userRepository.deleteAll();
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        channelService.addUserToChannel(channelId, userId);
    }

    @Override
    public void exitChannel(UUID channelId, UUID userId) {
        channelService.deleteUserFromChannel(channelId, userId);
    }
}

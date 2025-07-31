package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AddUserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final ChannelService channelService;

    public BasicUserService(UserRepository userRepository, ChannelService channelService) {
        this.userRepository = userRepository;
        this.channelService = channelService;
    }

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
        userRepository.findById(userId).ifPresent(userRepository::delete);
    }

    @Override
    public void deleteAllUser() {
        userRepository.deleteAll();
    }

    @Override
    public void joinChannel(UUID channelId, User user) {
        if(user == null){
            throw new IllegalArgumentException("user 파라미터가 null 입니다.");
        }
        channelService.addUserToChannel(channelId, user);
    }

    @Override
    public void exitChannel(UUID channelId, User user) {
        if(user == null){
            throw new IllegalArgumentException("user 파라미터가 null 입니다.");
        }
        channelService.deleteUserFromChannel(channelId, user);
    }
}

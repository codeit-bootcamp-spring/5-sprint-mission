package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.AddUserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final ChannelService channelService;
    private final UserRepository userRepository;

    public JCFUserService(ChannelService channelService, UserRepository userRepository) {
        this.channelService = channelService;
        this.userRepository = userRepository;
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
    public void joinChannel(UUID userId, UUID channelId) {
        /**
         * 나중에 channel의 set에 데이터 넣는 코드 작성해야함
         */

    }
}

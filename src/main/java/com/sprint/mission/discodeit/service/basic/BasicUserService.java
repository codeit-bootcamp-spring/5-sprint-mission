package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddUserDto;
import com.sprint.mission.discodeit.dto.response.GetUserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements com.sprint.mission.discodeit.service.UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User addUser(AddUserDto addUserDto) {
        Optional<User> byUserName = userRepository.findByUserName(addUserDto.userName());
        Optional<User> byEmail = userRepository.findByEmail(addUserDto.email());
        if(byEmail.isPresent() || byUserName.isPresent()){
            throw new IllegalArgumentException("중복된 User Name 혹은 User Email");
        }

        User user = new User(addUserDto.userName(), addUserDto.email(), addUserDto.password(), addUserDto.phoneNumber(), addUserDto.profileId());
        UserStatus userStatus = new UserStatus(user.getId());
        Optional<User> addedUser = userRepository.save(user);
        userStatusRepository.save(userStatus).orElseThrow();
        return addedUser.orElseThrow();
    }

    @Override
    public GetUserDto getUserById(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow();

        return new GetUserDto(user.getId() ,user.getUserName(), user.getEmail(), user.getPhoneNumber(), user.getProfileId(), userStatus.isOnline());
    }

    @Override
    public List<GetUserDto> getAllUser() {
        List<User> users = userRepository.findAll();
        List<GetUserDto> returnList = new ArrayList<>();
        for(User user: users){
            UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow();
            GetUserDto getUserDto = new GetUserDto(
                    user.getId(),
                    user.getUserName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getProfileId(),
                    userStatus.isOnline());
            returnList.add(getUserDto);
        }
        return returnList;
    }

    @Override
    public User updateUser(UUID userId, AddUserDto addUserDto) {
        User user = userRepository.findById(userId).orElseThrow();

        user.updateUserName(addUserDto.userName());
        user.updateEmail(addUserDto.email());
        user.updatePassword(addUserDto.password());
        user.updatePhoneNumber(addUserDto.phoneNumber());
        user.updateProfileId(addUserDto.profileId());

        return userRepository.save(user).orElseThrow();
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        userStatusRepository.deleteByUserId(userId);
        binaryContentRepository.deleteById(user.getProfileId());
        userRepository.delete(userId);
    }

    @Override
    public void deleteAllUser() {
        userStatusRepository.deleteAll();
        binaryContentRepository.deleteAll();
        userRepository.deleteAll();
    }
}

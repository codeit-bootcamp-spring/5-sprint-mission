package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicChannelService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Channel createChannel(String name) {
        // 1. 채널 이름 중복 검사
        Optional<Channel> existingChannel = channelRepository.findByName(name);
        if (existingChannel.isPresent()) {
            System.out.println("[Ser]Channel already exists: " + name);
            return existingChannel.get();
        }

        // 2. 새로운 Channel 객체 생성
        Channel newChannel = new Channel(name);

        // 3. 새로운 Channel을 Repository에 저장
        channelRepository.save(newChannel);
        System.out.println("[Ser]Created channel: " + newChannel);
        return newChannel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        System.out.println("[Ser]Finding channel by id: " + id);
        return channelRepository.findById(id);
    }

    @Override
    public Optional<Channel> findByName(String name) {
        System.out.println("[Ser]Finding channel by name: " + name);
        return channelRepository.findByName(name);
    }

    @Override
    public List<Channel> findAll() {
        System.out.println("[Ser]Finding all channels" + channelRepository.findAll().size());
        return channelRepository.findAll();
    }

    @Override
    public boolean addUserToChannel(UUID channelID, UUID userID) {
        // 1. 채널과 사용자 존재 체크
        Optional<Channel> existingChannel = channelRepository.findById(channelID);
        Optional<User> existingUser = userRepository.findById(userID);

        if (existingChannel.isPresent()) {
            System.out.println("[Ser]Channel already exists(Channel with ID): " + channelID);
            return false;
        }

        if (existingUser.isPresent()) {
            System.out.println("[Ser]User already exists(User with ID): " + userID);
        }

        Channel channel = existingChannel.get();
        User user = existingUser.get();

        // 2. 이미 사용자가 가입되어 있는지 체크
        if (channel.isMember(userID)) {
            System.out.println("[Ser]Channel is already User: " + channelID + " and user ID: " + userID);
            return false;
        }

        // 3. 채널에 사용자를 추가
        channel.join(userID); // Channel 객체에 join으로 추가

        // 4. 저장소에 저장
        channelRepository.save(channel);
        System.out.println("[Ser]" + user.getName() + "Successfully add to channel: " + channel);
        return true;
    }

    @Override
    public boolean removeUserToChannel(UUID channelID, UUID userID) {
        // 1. 채널과 사용자 존재를 체크
        Optional<Channel> existingChannel = channelRepository.findById(channelID);
        Optional<User> existingUser = userRepository.findById(userID);

        if (existingChannel.isPresent()) {
            System.out.println("[Ser]Channel already exists(Channel with ID): " + channelID);
            return false;
        }

        if (existingUser.isPresent()) {
            System.out.println("[Ser]User already exists(User with ID): " + userID);
        }

        Channel channel = existingChannel.get();
        User user = existingUser.get();

        // 2. 이미 사용자가 가입되어 있는지 체크
        if (channel.isMember(userID)) {
            System.out.println("[Ser]Channel is already User: " + channelID + " and user ID: " + userID);
            return false;
        }

        // 3. 채널에 사용자를 내보내기
        channel.leave(userID); // Channel 객체에 leave로 내보냄

        // 4. 저장소에 저장
        channelRepository.save(channel);
        System.out.println("[Ser]" + user.getName() + "Successfully delete to channel: " + channel);
        return true;
    }

    @Override
    public void updatedName(UUID id, String name) {
        try {
            if (channelRepository.findByName(name).isPresent()) {
                System.out.println("[Ser]Channel already exists: " + name);
            } else {
                Channel channel = channelRepository.findByName(name).get();
                channel.updateName(name);
                channelRepository.save(new Channel(name));
                System.out.println("[Ser]Updated channel: " + channel);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UUID id) {
        if (channelRepository.findById(id).isPresent()) {
            System.out.println("[Ser]Channel delete: " + id);
            channelRepository.delete(id);
        } else {
            System.out.println("[Ser]Channel not found: " + id);
        }
    }
}

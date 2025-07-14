package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> users = new HashMap<>();

    // 유저 생성
    @Override
    public void create(User user) {
        users.put(user.getId(), user);
    }

    // 삭제되지 않은 유저 조회
    @Override
    public User findById(UUID id) {
        User user = users.get(id);
        if (user != null && !user.isDeleted()) {
            return user;
        }
        return null;
    }

    // 삭제되지 않은 모든 유저 리스트 반환
    @Override
    public List<User> findAll() {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (!user.isDeleted()) {
                result.add(user);
            }
        }
        return result;
    }

    // 유저 정보 업데이트 (삭제된 유저는 제외)
    @Override
    public void update(UUID id, String name, int age) {
        User user = users.get(id);
        if (user != null && !user.isDeleted()) {
            user.update(name, age);
        }
    }

    // 유저가 채널에 참가
    @Override
    public boolean joinChannel(UUID id, UUID channelId) {
        User user = findById(id);
        return user != null && user.joinChannel(channelId);
    }

    // 유저가 채널에서 나가기
    @Override
    public boolean leaveChannel(UUID id, UUID channelId) {
        User user = findById(id);
        return user != null && user.leaveChannel(channelId);
    }

    // 유저가 메시지 전송 기록
    @Override
    public boolean addMessage(UUID id, UUID messageId) {
        User user = findById(id);
        return user != null && user.addMessage(messageId);
    }

    // 유저 삭제 (소프트 삭제 적용)
    @Override
    public void delete(UUID id) {
        User user = users.get(id);
        if (user != null) {
            user.delete();
        }
    }

    // 유저가 특정 채널에 참가 중인지 확인
    @Override
    public boolean isUserInChannel(UUID userId, UUID channelId) {
        User user = findById(userId);
        return user != null && user.getChannelIds().contains(channelId);
    }

    // 삭제 여부와 무관하게 유저 존재 여부 확인
    public boolean exists(UUID id) {
        return users.containsKey(id);
    }
}

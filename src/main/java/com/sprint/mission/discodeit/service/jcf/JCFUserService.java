package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import javax.swing.text.html.Option;
import java.util.*;

import static com.sprint.mission.discodeit.util.Logger.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data = new HashMap<>();

    // 유저 생성
    @Override
    public User create(String username, int age, String email, String password) {
        User user = new User(username, age, email, password);
        this.data.put(user.getId(), user);
        log("createUser", String.format("유저 생성 완료 → ID: %s, 이름: '%s', 나이: %d", user.getId(), user.getName(), user.getAge()));
        return user;
    }

    // 삭제되지 않은 유저 조회
    @Override
    public User findById(UUID id, boolean log) {
        User user = data.get(id);

        if (user != null && !user.isDeleted()) {
            if (log) {
                log("findUserById", String.format("유저 조회 성공 → ID: %s, 이름: '%s', 나이: %d", user.getId(), user.getName(), user.getAge()));
            }

            return user;
        }

        if (log) {
            log("findUserById", String.format("조회 실패 → ID %s의 유저가 존재하지 않거나 삭제됨", id));
        }

        return null;
    }

    // 삭제되지 않은 모든 유저 리스트 반환
    @Override
    public List<User> findAll() {
        List<User> result = data.values().stream()
                .filter(user -> !user.isDeleted())
                .toList();

        log("findAllUsers", String.format("전체 유저 수: %d명", result.size()));
        return result;
    }

    // 유저 정보 업데이트 (삭제된 유저는 제외)
    @Override
    public User update(UUID id, String name, int age) {
        User user = data.get(id);

        if (user != null && !user.isDeleted()) {
            String oldName = user.getName();
            int oldAge = user.getAge();

            user.update(name, age);

            log("updateUser", String.format("유저 수정 완료 → 이름: '%s' → '%s', 나이: %d → %d", oldName, name, oldAge, age));
        } else {
            log("updateUser", String.format("수정 실패 → ID %s의 유저가 존재하지 않거나 삭제됨", id));
        }

        return user;
    }

    // 유저가 채널에 참가
    @Override
    public boolean joinChannel(UUID id, UUID channelId) {
        User user = findById(id, false);
        return user != null && user.joinChannel(channelId);
    }

    // 유저가 채널에서 나가기
    @Override
    public boolean leaveChannel(UUID id, UUID channelId) {
        User user = findById(id, false);
        return user != null && user.leaveChannel(channelId);
    }

    // 유저가 메시지 전송 기록
    @Override
    public boolean addMessage(UUID id, UUID messageId) {
        User user = findById(id, false);
        return user != null && user.addMessage(messageId);
    }

    // 유저 삭제 (소프트 삭제 적용)
    @Override
    public void delete(UUID id) {
        User user = data.get(id);
        if (user != null && !user.isDeleted()) {
            user.delete();
            log("deleteUser", String.format("유저 삭제 완료 → 이름: '%s', ID: %s", user.getName(), user.getId()));
        } else {
            log("deleteUser", String.format("삭제 실패 → ID %s의 유저가 존재하지 않거나 이미 삭제됨", id));
        }
    }

    // 유저가 특정 채널에 참가 중인지 확인
    @Override
    public boolean isUserInChannel(UUID userId, UUID channelId) {
        User user = findById(userId, false);
        return user != null && user.getChannelIds().contains(channelId);
    }

    // 삭제 여부와 무관하게 유저 존재 여부 확인
    public boolean exists(UUID id) {
        return data.containsKey(id);
    }
}
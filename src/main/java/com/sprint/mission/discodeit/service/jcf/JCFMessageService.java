package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;
    // 의존성 주입: 관계된 데이터 존재 여부 확인용
    private final Map<UUID, User> userData;
    private final Map<UUID, Channel> channelData;


    public JCFMessageService(Map<UUID, User> userData, Map<UUID, Channel> channelData) {
        this.userData = userData;
        this.channelData = channelData;
        this.data = new HashMap<>();
    }

    @Override
    public void save(Message message) {
        if (!userData.containsKey(message.getId())) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (!channelData.containsKey(message.getId())) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        data.put(message.getId(), message);
    }

    @Override
    public Message find(UUID id) {
        return data.get(id);
    }

    @Override
    public ArrayList<Message> allFind() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, Message message) {
        if (data.containsKey(id)){
            data.put(id, message);
        }
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}

package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {

    private final  Map<UUID, Message> data = new HashMap<>();

    @Override
    public void save(Map<UUID, Message> data) {
        this.data.clear();
        this.data.putAll(data);
    }

    @Override
    public Map<UUID, Message> loadData() {
        return new HashMap<>(data);
    }

    @Override
    public void clear() {
        this.data.clear();
    }
}

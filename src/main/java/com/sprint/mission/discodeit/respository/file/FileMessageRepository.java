package com.sprint.mission.discodeit.respository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.respository.MessageRepository;
import java.util.*;

public class FileMessageRepository extends FileStore<Message> implements MessageRepository {

    private final Map<UUID, Message> messageMap = new HashMap<>();

    public FileMessageRepository() {
        super("data/message.store");
        Map<UUID, Message> loaded = loadFromFile();
        messageMap.putAll(loaded);
    }

    @Override
    public Message save(Message message) {
        messageMap.put(message.getId(), message);
        saveToFile(messageMap);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return messageMap.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public List<Message> findByStr(String str) {
        List<Message> result = new ArrayList<>();
        for (Message message : messageMap.values()) {
            if (message.getContent().contains(str)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public boolean deleteById(UUID id) {
        boolean removed = messageMap.remove(id) != null;
        if (removed) {
            saveToFile(messageMap);
        }
        return removed;
    }

}

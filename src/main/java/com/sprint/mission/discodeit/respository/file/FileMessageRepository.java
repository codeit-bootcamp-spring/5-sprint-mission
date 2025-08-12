package com.sprint.mission.discodeit.respository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.respository.MessageRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


public class FileMessageRepository extends FileStore<Message> implements MessageRepository {

    private final Map<UUID, Message> data = new HashMap<>();

    public FileMessageRepository(String rootDir) {
        super(rootDir + "message.ser");
        Map<UUID, Message> loaded = loadFromFile();
        data.putAll(loaded);
    }

    // 메시지 저장
    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        saveToFile(data);
        return message;
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }


    @Override
    public List<Message> findByContent(String str) {
        List<Message> result = new ArrayList<>();
        for (Message message : data.values()) {
            if (message.getContent().contains(str)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public Optional<Instant> findLastCreatedAtByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder());
    }

    // 해당 채널의 모든 메시지 조회
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt)) // optional: 정렬
                .collect(Collectors.toList());
    }

    // 특정 시점 이후의 메시지만 조회
    @Override
    public List<Message> findAllByChannelIdAfter(UUID channelId, Instant after) {
        return data.values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .filter(m -> m.getCreatedAt().isAfter(after))
                .sorted(Comparator.comparing(Message::getCreatedAt)) // optional: 정렬
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(UUID id) {
        boolean removed = data.remove(id) != null;
        if (removed) {
            saveToFile(data);
        }
        return removed;
    }

}

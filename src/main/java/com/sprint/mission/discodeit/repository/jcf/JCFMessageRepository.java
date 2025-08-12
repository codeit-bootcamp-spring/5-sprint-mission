package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFMessageRepository implements MessageRepository {
    
    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findByChannel(UUID channelId) {
        return data.values().stream()
                .filter(m -> m.getChannelId().equals(channelId)).collect(Collectors.toList());
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data.values());
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
        return data.remove(id) != null;
    }
}

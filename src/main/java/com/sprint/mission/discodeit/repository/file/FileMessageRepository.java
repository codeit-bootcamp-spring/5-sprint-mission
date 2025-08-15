package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
@Profile("dev")
public class FileMessageRepository extends FileBaseRepository<Message> implements MessageRepository {

    public FileMessageRepository(AppProperties appProperties) {
        super(Message.class, appProperties.storage());
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId must not be null");
        return findAll().stream()
                .filter(m -> channelId.equals(m.getChannelId()))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public List<Message> findRecentByChannelId(UUID channelId, int limit) {
        Objects.requireNonNull(channelId, "channelId must not be null");
        int lim = normalizeLimit(limit);
        if (lim == 0) return List.of();
        return findAll().stream()
                .filter(m -> channelId.equals(m.getChannelId()))
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .limit(lim)
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public List<Message> findAllByAuthorId(UUID authorId) {
        Objects.requireNonNull(authorId, "authorId must not be null");
        return findAll().stream()
                .filter(m -> authorId.equals(m.getAuthorId()))
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Message> findAllReplies(UUID replyTo) {
        Objects.requireNonNull(replyTo, "replyTo must not be null");
        return findAll().stream()
                .filter(m -> replyTo.equals(m.getReplyTo()))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public List<Message> searchInChannel(UUID channelId, String keyword) {
        Objects.requireNonNull(channelId, "channelId must not be null");
        String k = MessageRepository.normalizeKeyword(keyword);
        if (k.isEmpty()) return List.of();
        return findAll().stream()
                .filter(m -> channelId.equals(m.getChannelId()))
                .filter(m -> {
                    String c = m.getContent();
                    return c != null && c.toLowerCase().contains(k);
                })
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public int softDeleteAllByChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId must not be null");
        var ids = findAll().stream()
                .filter(m -> channelId.equals(m.getChannelId()))
                .map(Message::getId)
                .collect(java.util.stream.Collectors.toSet());
        return softDeleteAllByIds(ids);
    }

    @Override
    public int softDeleteAllByAuthorId(UUID authorId) {
        Objects.requireNonNull(authorId, "authorId must not be null");
        var ids = findAll().stream()
                .filter(m -> authorId.equals(m.getAuthorId()))
                .map(Message::getId)
                .collect(java.util.stream.Collectors.toSet());
        return softDeleteAllByIds(ids);
    }

    @Override
    public long countByChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId must not be null");
        return findAll().stream().filter(m -> channelId.equals(m.getChannelId())).count();
    }
}

package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.Message;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public interface MessageRepository extends BaseRepository<Message> {

    List<Message> findAllByChannelId(UUID channelId);

    List<Message> findRecentByChannelId(UUID channelId, int limit);

    List<Message> findAllByAuthorId(UUID authorId);

    List<Message> findAllReplies(UUID replyTo);

    List<Message> searchInChannel(UUID channelId, String keyword);

    int softDeleteAllByChannelId(UUID channelId);

    int softDeleteAllByAuthorId(UUID authorId);

    long countByChannelId(UUID channelId);

    default int normalizeLimit(int limit) {
        return Math.max(0, limit);
    }

    static String normalizeKeyword(String keyword) {
        Objects.requireNonNull(keyword, "keyword must not be null");
        return keyword.strip().toLowerCase();
    }
}

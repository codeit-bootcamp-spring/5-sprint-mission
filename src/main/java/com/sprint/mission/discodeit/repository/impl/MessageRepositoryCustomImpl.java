package com.sprint.mission.discodeit.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.QMessage;
import com.sprint.mission.discodeit.repository.MessageRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Message> findByChannelIdWithCursor(UUID channelId, Instant cursor, int size) {
        return queryFactory
                .selectFrom(QMessage.message)
                .leftJoin(QMessage.message.author).fetchJoin()
                .leftJoin(QMessage.message.attachments).fetchJoin()
                .where(
                        QMessage.message.channel.id.eq(channelId),
                        QMessage.message.createdAt.lt(cursor)
                )
                .orderBy(QMessage.message.createdAt.desc())
                .limit(size + 1)
                .fetch();
    }

}

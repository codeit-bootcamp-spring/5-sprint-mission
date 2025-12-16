package com.sprint.mission.discodeit.repository.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.QMessage;
import com.sprint.mission.discodeit.repository.MessageQueryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MessageQueryRepositoryImpl implements MessageQueryRepository {
	private final JPAQueryFactory queryFactory;
	private static final QMessage m = QMessage.message;

	@Override
	public Slice<Message> search(UUID channelId, Instant createdAt, Pageable pageable) {

		int limit = pageable.getPageSize();

		BooleanBuilder where = new BooleanBuilder();
		where.and(m.channel.id.eq(channelId));

		if (createdAt != null) {
			where.and(m.createdAt.lt(createdAt));
		}

		List<Message> rowPlusOne = queryFactory.selectFrom(m)
			.where(where)
			.orderBy(m.createdAt.desc())
			.limit(limit + 1)
			.fetch();

		boolean hasNext = rowPlusOne.size() > limit;
		List<Message> content = hasNext ? rowPlusOne.subList(0, limit) : rowPlusOne;

		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public Optional<Message> findLastMessage(UUID channelId) {
		Message message = queryFactory.selectFrom(m)
			.where(m.channel.id.eq(channelId))
			.orderBy(m.createdAt.desc())
			.limit(1)
			.fetchOne();

		return Optional.ofNullable(message);
	}
}

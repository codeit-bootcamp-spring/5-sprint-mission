package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

// Message 엔티티에 대한 DB 접근을 담당하는 Repository 인터페이스
// JpaRepository<Message, UUID>를 상속받아 기본 CRUD 메서드를 제공
public interface MessageRepository extends JpaRepository<Message, UUID> {

    // 특정 채널에 속한 메시지 목록을 조회하는 JPQL 쿼리
    // - 작성자(author)와 그 상태(status), 프로필(profile)까지 한 번에 패치 조인(fetch join)
    // - createdAt 기준으로 cursor 페이징(특정 시간 이전의 메시지만 조회)
    // - Slice<Message> 형태로 반환 → 전체 개수(count)를 구하지 않고 다음 페이지 여부만 확인 가능
    @Query("SELECT m FROM Message m "
            + "LEFT JOIN FETCH m.author a "
            + "JOIN FETCH a.status "
            + "LEFT JOIN FETCH a.profile "
            + "WHERE m.channel.id=:channelId AND m.createdAt < :createdAt")
    Slice<Message> findAllByChannelIdWithAuthor(@Param("channelId") UUID channelId,
                                                @Param("createdAt") Instant createdAt,
                                                Pageable pageable);

    // 특정 채널에서 가장 마지막(최신) 메시지의 작성 시각(createdAt)을 조회
    // - 전체 Message 엔티티가 아닌 createdAt 필드만 Projection
    // - 최신순 정렬 후 가장 첫 번째 값만 반환
    // - Optional<Instant> 형태로 반환하여 메시지가 없을 경우 빈 값 처리 가능
    @Query("SELECT m.createdAt "
            + "FROM Message m "
            + "WHERE m.channel.id = :channelId "
            + "ORDER BY m.createdAt DESC LIMIT 1")
    Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

    // 특정 채널에 속한 모든 메시지를 삭제
    // SQL로 치면: DELETE FROM message WHERE channel_id = ?
    void deleteAllByChannelId(UUID channelId);
}

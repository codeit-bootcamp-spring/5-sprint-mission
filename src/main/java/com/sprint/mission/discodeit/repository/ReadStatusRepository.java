package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// ReadStatus 엔티티에 대한 DB 접근을 담당하는 Repository 인터페이스
// JpaRepository<ReadStatus, UUID>를 상속받아 기본 CRUD 메서드를 제공
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    // 특정 사용자(userId)가 가진 모든 ReadStatus 조회
    List<ReadStatus> findAllByUserId(UUID userId);

    // 특정 채널(channelId)에 속한 ReadStatus들을 조회하면서
    // - User, UserStatus, UserProfile까지 패치 조인(fetch join)하여 한 번에 가져옴
    // → N+1 문제를 방지하기 위함
    @Query("SELECT r FROM ReadStatus r "
            + "JOIN FETCH r.user u "
            + "JOIN FETCH u.status "
            + "LEFT JOIN FETCH u.profile "
            + "WHERE r.channel.id = :channelId")
    List<ReadStatus> findAllByChannelIdWithUser(@Param("channelId") UUID channelId);

    // 특정 사용자(userId)가 특정 채널(channelId)에 이미 참여(읽음 상태 존재)하고 있는지 여부 확인
    Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    // 특정 채널(channelId)에 속한 모든 ReadStatus 삭제
    void deleteAllByChannelId(UUID channelId);
}

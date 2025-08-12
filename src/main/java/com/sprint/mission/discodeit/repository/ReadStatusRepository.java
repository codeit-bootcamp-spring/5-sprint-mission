package com.sprint.mission.discodeit.repository; // 레포지토리 패키지 선언

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ReadStatus 전용 레포지토리 인터페이스
 * - 사용자별/채널별 마지막 읽은 시각 조회에 사용
 */
public interface ReadStatusRepository { // ReadStatus 저장소 계약 정의 시작

    ReadStatus save(ReadStatus entity); // ReadStatus 저장(신규/갱신 모두 save로 처리)

    Optional<ReadStatus> findById(UUID id); // 식별자로 단건 조회

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId); // 사용자+채널 기준 단건 조회

    List<ReadStatus> findAllByUserId(UUID userId); // 특정 사용자의 모든 채널 읽음 상태 목록 조회

    void deleteById(UUID id); // 식별자로 삭제

    boolean existsById(UUID id);

}

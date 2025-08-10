package com.sprint.mission.discodeit.repository; // 레포지토리 패키지 선언

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * BinaryContent 전용 레포지토리 인터페이스(불변 도메인)
 * - updatedAt이 없으므로 생성/조회/삭제 중심
 * - Message의 attachmentIds, User의 profileId가 이 ID를 참조
 */
public interface BinaryContentRepository { // BinaryContent 저장소 계약 정의 시작

    BinaryContent save(BinaryContent entity); // 신규 BinaryContent 저장(불변이므로 갱신 개념 없음)

    Optional<BinaryContent> findById(UUID id); // 식별자로 단건 조회

    List<BinaryContent> findAllById(Collection<UUID> ids); // 다건 식별자 기반 일괄 조회(첨부파일 목록 조회에 유용)

    void deleteById(UUID id); // 식별자로 삭제(파일 정리 등)

    boolean existsById(UUID id);

}

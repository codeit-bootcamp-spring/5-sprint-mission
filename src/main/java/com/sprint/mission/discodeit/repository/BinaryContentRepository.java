package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * BinaryContent 전용 레포지토리
 * - 기본 CRUD는 JpaRepository가 제공
 * - 첨부파일 일괄 조회용 쿼리 메서드만 선언
 */
public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
}

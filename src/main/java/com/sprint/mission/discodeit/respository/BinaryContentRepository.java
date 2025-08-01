package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    // 상태 저장(갱신)
    void save(BinaryContent binaryContent);

    // 파일 조회
    Optional<BinaryContent> findById(UUID id);

    // 전체 상태 조회
    List<BinaryContent> findAll();

}

package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.service.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.service.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.service.dto.readstatus.ReadStatusUpdateRequest;

import java.util.List;

public interface ReadStatusService {

    // create
    Long create(ReadStatusCreateRequest request);

    // find
    ReadStatusResponse find(Long id);

    // findAllByUserId
    List<ReadStatusResponse> findAllByUserId(Long userId);

    // update (id는 메서드 파라미터, 수정값은 DTO)
    void update(Long id, ReadStatusUpdateRequest request);

    // delete
    void delete(Long id);
}

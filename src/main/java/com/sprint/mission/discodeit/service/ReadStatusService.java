package com.sprint.mission.discodeit.service;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDto;
=======
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
<<<<<<< HEAD
    ReadStatusResponseDto create(ReadStatusCreateRequest request);

    ReadStatusResponseDto find(UUID id);

    List<ReadStatusResponseDto> findAllByUserId(UUID userId);

    List<ReadStatusResponseDto> findAllByChannelId(UUID channelId);

    ReadStatusResponseDto update(UUID id, ReadStatusUpdateRequest request);

    void delete(UUID id);
=======

  ReadStatusDto create(ReadStatusCreateRequest request);

  ReadStatusDto find(UUID readStatusId);

  List<ReadStatusDto> findAllByUserId(UUID userId);

  ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request);

  void delete(UUID readStatusId);
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}

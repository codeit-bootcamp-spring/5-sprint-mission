package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("basicReadStatusService")
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;


    @Override
    public ReadStatus create(ReadStatus readStatus) {
        return readStatusRepository.save(readStatus);
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.findByUserIdAndChannelId(userId, channelId);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return readStatusRepository.findAllByChannelId(channelId);
    }

    @Override
    public ReadStatus update(ReadStatus readStatus) {
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID messageId) {
        readStatusRepository.deleteById(messageId);
    }

    @Override
    public ReadStatus create(ReadStatusCreateRequest readStatusCreateRequest) {
        Instant lastReadAt = readStatusCreateRequest.lastReadAt() != null ? readStatusCreateRequest.lastReadAt() : Instant.now();
        ReadStatus readStatus = new ReadStatus(readStatusCreateRequest.userId(), readStatusCreateRequest.channelId(), lastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus update(UUID id, ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(readStatusUpdateRequest.userId(), readStatusUpdateRequest.channelId())
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus가 존재하지 않습니다."));
        readStatus.readUpdate(readStatusUpdateRequest.lastReadAt());
        return readStatusRepository.save(readStatus);
    }
}

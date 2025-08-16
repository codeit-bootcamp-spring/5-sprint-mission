package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest readStatusCreateRequest) {
        UUID userId = readStatusCreateRequest.userId();
        UUID channelId = readStatusCreateRequest.channelId();

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found with id " + userId);
        }
        if (readStatusRepository.findByUserId(userId).stream()
                .anyMatch(readStatus -> readStatus.getChannelId().equals(channelId))) {
            throw new IllegalArgumentException("userId and channelId already exists");
        }

        Instant lastReadAt = Instant.now();
        ReadStatus readStatus = new ReadStatus(userId, channelId, lastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id " + readStatusId));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest readStatusUpdateRequest) {
        Instant newLastReadAt = readStatusUpdateRequest.newLastReadAt();

        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id " + readStatusId));

        readStatus.updateReadStatus(newLastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {

    }
}

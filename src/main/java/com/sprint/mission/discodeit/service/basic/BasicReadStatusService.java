package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("User not found with id: " + request.getUserId());
        }
        if (!channelRepository.existsById(request.getChannelId())) {
            throw new IllegalArgumentException("Channel not found with id: " + request.getChannelId());
        }
        if (readStatusRepository.findAllByUserId(request.getUserId()).stream()
                .anyMatch(readStatus -> readStatus.getChannelId().equals(request.getChannelId()))) {
            throw new IllegalArgumentException("ReadStatus for user " + request.getUserId() + " and channel " + request.getChannelId() + " already exists.");
        }

        ReadStatus readStatus = ReadStatus.builder()
                .userId(request.getUserId())
                .channelId(request.getChannelId())
                .lastReadAt(request.getLastReadAt())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        ReadStatus savedReadStatus = readStatusRepository.save(readStatus);
        return toReadStatusResponse(savedReadStatus);
    }

    @Override
    public ReadStatusResponse find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + id + " not found"));
        return toReadStatusResponse(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(this::toReadStatusResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + request.getId() + " not found"));

        if (request.getLastReadAt() != null) {
            readStatus.setLastReadAt(request.getLastReadAt());
        }
        readStatus.setUpdatedAt(Instant.now());
        ReadStatus updatedReadStatus = readStatusRepository.save(readStatus);
        return toReadStatusResponse(updatedReadStatus);
    }

    @Override
    public void delete(UUID id) {
        if (!readStatusRepository.existsById(id)) {
            throw new NoSuchElementException("ReadStatus with id " + id + " not found");
        }
        readStatusRepository.deleteById(id);
    }

    @Override
    public void clear() {
        readStatusRepository.clear();
    }

    private ReadStatusResponse toReadStatusResponse(ReadStatus readStatus) {
        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt()
        );
    }
}
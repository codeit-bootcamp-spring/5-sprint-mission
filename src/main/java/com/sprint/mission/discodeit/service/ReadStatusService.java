package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public ReadStatusResponseDto create(ReadStatusCreateRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("User not found with id " + request.userId());
        }
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + request.channelId());
        }

        boolean exists = readStatusRepository.findByUserId(request.userId()).stream()
                .anyMatch(rs -> rs.getChannelId().equals(request.channelId()));
        if (exists) {
            throw new IllegalStateException("ReadStatus already exists for user " + request.userId() + " and channel " + request.channelId());
        }

        ReadStatus newReadStatus = new ReadStatus(
                UUID.randomUUID(),
                request.userId(),
                request.channelId(),
                Instant.now(),
                Instant.now()
        );
        ReadStatus saved = readStatusRepository.save(newReadStatus);

        return new ReadStatusResponseDto(
                saved.getId(),
                saved.getUserId(),
                saved.getChannelId(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    public ReadStatusResponseDto find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id " + id));

        return new ReadStatusResponseDto(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt()
        );
    }

    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found with id " + userId);
        }
        List<ReadStatus> list = readStatusRepository.findByUserId(userId);
        return list.stream()
                .map(status -> new ReadStatusResponseDto(
                        status.getId(),
                        status.getUserId(),
                        status.getChannelId(),
                        status.getCreatedAt(),
                        status.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    public ReadStatusResponseDto update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id " + request.id()));

        readStatus.update();
        ReadStatus updated = readStatusRepository.save(readStatus);

        return new ReadStatusResponseDto(
                updated.getId(),
                updated.getUserId(),
                updated.getChannelId(),
                updated.getCreatedAt(),
                updated.getUpdatedAt()
        );
    }

    public void delete(UUID id) {
        if (!readStatusRepository.existsById(id)) {
            throw new NoSuchElementException("ReadStatus not found with id " + id);
        }
        readStatusRepository.deleteById(id);
    }
}

package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.status.read.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.status.read.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.status.read.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ConflictException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(CreateReadStatusRequest request) {
        if (userRepository.findById(request.userId()).isEmpty()) throw new NotFoundException("User not found: " + request.userId());
        if (channelRepository.findById(request.channelId()).isEmpty()) throw new NotFoundException("Channel not found: " + request.channelId());
        if (readStatusRepository.existsByUserIdAndChannelId(request.userId(), request.channelId())) throw new ConflictException("ReadStatus already exists for this user & channel");

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId(), request.lastReadAt());
        readStatusRepository.save(readStatus);

        return toResponse(readStatus);
    }

    @Override
    public Optional<ReadStatusResponse> getById(UUID id) {
        return readStatusRepository.findById(id).map(this::toResponse);
    }

    @Override
    public List<ReadStatusResponse> getAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ReadStatusResponse update(UpdateReadStatusRequest request) {
        Optional<ReadStatus> optionalReadStatus = readStatusRepository.findById(request.id());
        if (optionalReadStatus.isEmpty()) return null;

        ReadStatus readStatus = optionalReadStatus.get();
        readStatus.updateLastReadAt(request.lastReadAt());
        return toResponse(readStatus);
    }

    @Override
    public boolean delete(UUID id) {
        return readStatusRepository.delete(id);
    }

    private ReadStatusResponse toResponse(ReadStatus readStatus) {
        return new ReadStatusResponse(readStatus.getId(), readStatus.getUserId(), readStatus.getChannelId(), readStatus.getLastReadAt());
    }
}

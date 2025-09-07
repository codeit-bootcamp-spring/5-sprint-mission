package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusResponseDto create(ReadStatusCreateRequest request) {
        var user = userRepository.findById(request.userId())
            .orElseThrow(() -> new NoSuchElementException("User not found with id " + request.userId()));
        var channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> new NoSuchElementException("Channel not found with id " + request.channelId()));

        boolean exists = readStatusRepository.findByUserId(user.getId()).stream()
            .anyMatch(rs -> rs.getChannel().getId().equals(channel.getId()));
        if (exists) {
            throw new IllegalStateException("ReadStatus already exists for user " + user.getId() + " and channel " + channel.getId());
        }

        ReadStatus newReadStatus = new ReadStatus();
        newReadStatus.setUser(user);
        newReadStatus.setChannel(channel);
        newReadStatus.setLastReadAt(Instant.now());

        ReadStatus saved = readStatusRepository.save(newReadStatus);
        return readStatusMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusResponseDto find(UUID id) {
        return readStatusRepository.findById(id)
            .map(readStatusMapper::toDto)
            .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id " + userId));
        return readStatusRepository.findByUserId(user.getId()).stream()
            .map(readStatusMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusResponseDto> findAllByChannelId(UUID channelId) {
        var channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("Channel not found with id " + channelId));
        return readStatusRepository.findByChannelId(channel.getId()).stream()
            .map(readStatusMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public ReadStatusResponseDto update(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id " + id));

        readStatus.update(request.newLastReadAt());
        // save() 불필요 → 변경감지
        return readStatusMapper.toDto(readStatus);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!readStatusRepository.existsById(id)) {
            throw new NoSuchElementException("ReadStatus not found with id " + id);
        }
        readStatusRepository.deleteById(id);
    }
}

package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.data.ReadStatusDto;
import com.codeit.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.codeit.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.entity.ReadStatus;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.mapper.ReadStatusMapper;
import com.codeit.mission.discodeit.repository.ChannelRepository;
import com.codeit.mission.discodeit.repository.ReadStatusRepository;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "User with id " + userId + " does not exist"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Channel with id " + channelId + " does not exist")
                );

        if (readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId())) {
            throw new IllegalArgumentException(
                    "ReadStatus with userId " + userId + " and channelId " + channelId
                            + " already exists");
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        readStatusRepository.save(readStatus);

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public ReadStatusDto find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .map(readStatusMapper::toDto)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "ReadStatus with id " + readStatusId + " not found"));
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        Instant newLastReadAt = request.newLastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "ReadStatus with id " + readStatusId + " not found"));
        readStatus.update(newLastReadAt);
        return readStatusMapper.toDto(readStatus);
    }

    @Transactional
    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        }
        readStatusRepository.deleteById(readStatusId);
    }
}

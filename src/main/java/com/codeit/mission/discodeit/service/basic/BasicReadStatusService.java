package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.codeit.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.entity.ReadStatus;
import com.codeit.mission.discodeit.entity.User;
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
@Transactional
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new NoSuchElementException("User with id " + userId + " does not exist"));
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException(
                "Channel with id " + channelId + " does not exist"));

        if (readStatusRepository.findAllByUserId(userId).stream()
            .anyMatch(readStatus -> readStatus.getChannel().getId().equals(channelId))) {
            throw new IllegalArgumentException(
                "ReadStatus with userId " + userId + " and channelId " + channelId
                    + " already exists");
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new NoSuchElementException(
                "ReadStatus with id " + readStatusId + " not found"));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
        Instant newLastReadAt = request.newLastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new NoSuchElementException(
                "ReadStatus with id " + readStatusId + " not found"));
        readStatus.update(newLastReadAt);
        return readStatus;
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus not found");
        }

        readStatusRepository.deleteById(readStatusId);
    }
}
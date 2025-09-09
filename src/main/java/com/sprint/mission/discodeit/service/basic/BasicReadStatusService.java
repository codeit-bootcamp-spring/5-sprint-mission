package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    @Transactional
    public ReadStatus create(ReadStatusCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new EntityNotFoundException("Channel not found"));

        if (readStatusRepository.existsByUserAndChannel(user, channel)) {
            throw new IllegalArgumentException("ReadStatus for this user and channel already exists");
        }

        ReadStatus readStatus = new ReadStatus(user, channel, request.lastReadAt());
        return readStatusRepository.save(readStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new EntityNotFoundException("ReadStatus not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadStatus> findAllByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return readStatusRepository.findAllByUser(user);
    }

    @Override
    @Transactional
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = find(readStatusId); // find 메서드 재활용

        readStatus.update(request.newLastReadAt());

        return readStatus;
    }

    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new EntityNotFoundException("ReadStatus not found");
        }
        readStatusRepository.deleteById(readStatusId);
    }
}

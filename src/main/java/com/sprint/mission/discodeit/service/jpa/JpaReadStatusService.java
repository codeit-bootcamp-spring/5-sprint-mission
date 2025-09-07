package com.sprint.mission.discodeit.service.jpa;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class JpaReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new NoSuchElementException("User not found"));
        Channel channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> new NoSuchElementException("Channel not found"));

        boolean exists = readStatusRepository.existsByUserAndChannel(user, channel);
        if (exists) {
            throw new IllegalArgumentException("ReadStatus already exists for this user & channel");
        }

        // 영속성 전이 활용: save는 필요 없음, Persist 시점에서 영속화
        ReadStatus readStatus = new ReadStatus(user, channel, request.lastReadAt());
        return toResponse(readStatusRepository.save(readStatus));
    }

    @Override
    @Transactional(readOnly = true)
    public ReadStatusResponse find(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new NoSuchElementException("ReadStatus not found"));
        return toResponse(readStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new NoSuchElementException("ReadStatus not found"));

        // Dirty Checking: save 호출 필요 없음
        readStatus.update(request.newLastReadAt());
        return toResponse(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new NoSuchElementException("ReadStatus not found"));
        readStatusRepository.delete(readStatus);
    }

    private ReadStatusResponse toResponse(ReadStatus readStatus) {
        return new ReadStatusResponse(
            readStatus.getId(),
            readStatus.getUser().getId(),
            readStatus.getChannel().getId(),
            readStatus.getLastReadAt()
        );
    }
}

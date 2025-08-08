package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
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
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service("BasicReadStatusService")
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    // CREATE -> UUID
    @Override
    public UUID create(ReadStatusCreateRequest request) {
        UUID userId = request.getUserId();
        UUID channelId = request.getChannelId();

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found with id: " + userId);
        }
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id: " + channelId);
        }
        if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new IllegalStateException("ReadStatus already exists for this user and channel.");
        }

        Instant now = Instant.now();
        Instant lastReadAt = Optional.ofNullable(request.getLastReadAt()).orElse(now);

        ReadStatus rs = new ReadStatus(
                UUID.randomUUID(),
                channelId,
                userId,
                now,
                now,
                lastReadAt
        );
        readStatusRepository.save(rs);
        return rs.getId();
    }

    // FIND -> Optional<ReadStatusResponse>
    @Override
    public Optional<ReadStatusResponse> find(UUID id) {
        return readStatusRepository.findById(id)
                .map(ReadStatusResponse::new);
    }

    // LIST BY USER -> List<ReadStatusResponse>
    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(ReadStatusResponse::new)
                .toList();
    }

    // LIST BY CHANNEL -> List<ReadStatusResponse>
    @Override
    public List<ReadStatusResponse> findAllByChannelId(UUID channelId) {
        return readStatusRepository.findAllByChannelId(channelId)
                .stream()
                .map(ReadStatusResponse::new)
                .toList();
    }

    // UPDATE -> boolean
    @Override
    public boolean update(ReadStatusUpdateRequest request) {
        ReadStatus rs = readStatusRepository.findById(request.getId())
                .orElseThrow(() ->
                        new NoSuchElementException("ReadStatus not found with id: " + request.getId()));

        rs.update(request.getLastReadAt());   // 엔티티에 lastReadAt/updatedAt 갱신 로직 있음
        readStatusRepository.save(rs);
        return true;
    }

    // DELETE -> boolean
    @Override
    public boolean delete(UUID id) {
        return readStatusRepository.deleteById(id);
    }
}

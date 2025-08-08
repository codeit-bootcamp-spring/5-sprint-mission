package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service("BasicReadStatusService")
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new NoSuchElementException("User not found with id: " + request.getUserId());
        }
        if (!channelRepository.existsById(request.getChannelId())) {
            throw new NoSuchElementException("Channel not found with id: " + request.getChannelId());
        }
        if (readStatusRepository.existsByUserIdAndChannelId(request.getUserId(), request.getChannelId())) {
            throw new IllegalStateException("ReadStatus already exists for this user and channel.");
        }

        ReadStatus readStatus = new ReadStatus(
                request.getUserId(),
                request.getChannelId(),
                request.getLastReadAt()
        );
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(ReadStatus id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id: " + id));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id: " + request.getId()));

        readStatus.update(request.getLastReadAt());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public boolean delete(ReadStatus id) {
        if (!readStatusRepository.deleteById(id)) {
            throw new NoSuchElementException("ReadStatus not found or already deleted with id: " + id);
        }
        return false;
    }
}


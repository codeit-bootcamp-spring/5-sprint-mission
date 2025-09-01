package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.readStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.readstatus.AlreadyExistsReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        if (!userRepository.existsById(request.getUserId())
                || channelRepository.findById(request.getChannelId()).isEmpty()) {
            throw new ReadStatusNotFoundException();
        }


        if (readStatusRepository.existsByChannelIdAndUserId(request.getChannelId(), request.getUserId())) {

            ReadStatus existingReadStatus = readStatusRepository.findByChannelIdAndUserId(request.getChannelId(), request.getUserId());

            existingReadStatus.update(request.getLastReadAt());
            readStatusRepository.save(existingReadStatus);

            return ReadStatusResponse.success(existingReadStatus);
        } else {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + request.getUserId()));

            Channel channel = channelRepository.findById(request.getChannelId())
                    .orElseThrow(() -> new NoSuchElementException("Channel not found: " + request.getChannelId()));

            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);

            return ReadStatusResponse.success(readStatus);
        }
    }

    @Override
    public ReadStatusResponse getById(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(ReadStatusNotFoundException::new);
        return ReadStatusResponse.success(readStatus);
    }

    @Override
    public List<ReadStatusResponse> getAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findByUserId(userId);

        return readStatuses.stream()
                .map(ReadStatusResponse::success)
                .toList();
    }

    @Override
    public List<ReadStatusResponse> getAllByChannelId(UUID channelId) {
        List<ReadStatus> readStatuses = readStatusRepository.findByUserId(channelId);

        return readStatuses.stream()
                .map(ReadStatusResponse::success)
                .toList();
    }

    @Override
    public ReadStatusResponse updateById(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(ReadStatusNotFoundException::new);

        readStatus.update(request.getNewLastReadAt());
        readStatusRepository.save(readStatus);

        return ReadStatusResponse.success(readStatus);
    }

    @Override
    public ReadStatusResponse updateByChannelIdAndUserId(UUID channelId, UUID userId,
                                                         ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findByChannelIdAndUserId(channelId, userId);
        if (readStatus == null) {
            throw new ReadStatusNotFoundException();
        }

        readStatus.update(request.getNewLastReadAt());
        readStatusRepository.save(readStatus);

        return ReadStatusResponse.success(readStatus);
    }

    @Override
    public ReadStatusResponse delete(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(ReadStatusNotFoundException::new);

        readStatusRepository.deleteById(id);

        return ReadStatusResponse.success(readStatus);
    }
}

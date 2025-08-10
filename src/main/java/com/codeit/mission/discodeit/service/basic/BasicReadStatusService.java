package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.codeit.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.codeit.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.codeit.mission.discodeit.entity.ReadStatus;
import com.codeit.mission.discodeit.repository.ChannelRepository;
import com.codeit.mission.discodeit.repository.ReadStatusRepository;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.service.ReadStatusService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("basicReadStatusService")
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicReadStatusService(@Qualifier("readStatusRepository") ReadStatusRepository readStatusRepository,
                                  @Qualifier("userRepository") UserRepository userRepository,
                                  @Qualifier("channelRepository") ChannelRepository channelRepository) {
        this.readStatusRepository = readStatusRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        if (!channelRepository.existsById(request.getChannelId())) {
            throw new NoSuchElementException("Channel not found");
        }
        if (!userRepository.existsById(request.getUserId())) {
            throw new NoSuchElementException("User not found");
        }

        boolean alreadyExists = readStatusRepository.findAll().stream()
                .anyMatch(readStatus ->
                        readStatus.getChannelId().equals(request.getChannelId()) &&
                                readStatus.getUserId().equals(request.getUserId())
                );

        if (alreadyExists) {
            throw new IllegalArgumentException("ReadStatus already exists for this user and channel");
        }

        ReadStatus readStatus = new ReadStatus(request.getUserId(), request.getChannelId(), request.getLastReadTime());
        ReadStatus savedReadStatus = readStatusRepository.save(readStatus);

        return new ReadStatusResponse(savedReadStatus);
    }

    @Override
    public ReadStatusResponse find(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found"));

        return new ReadStatusResponse(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAll();

        return readStatuses.stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .map(ReadStatusResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.getReadStatusId())
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found"));

        readStatus.update(request.getLastReadTime());
        ReadStatus savedReadStatus = readStatusRepository.save(readStatus);

        return new ReadStatusResponse(savedReadStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus not found");
        }

        readStatusRepository.deleteById(readStatusId);
    }
}
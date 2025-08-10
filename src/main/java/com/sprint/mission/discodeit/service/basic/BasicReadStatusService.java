package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
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
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new NoSuchElementException("존재하지 않는 사용자입니다.");
        }

        if (!channelRepository.existsById(request.getChannelId())) {
            throw new NoSuchElementException("존재하지 않는 채널입니다.");
        }

        if (readStatusRepository.findByUserIdAndChannelId(request.getUserId(), request.getChannelId()).isPresent()) {
            throw new RuntimeException("이미 존재하는 ReadStatus입니다.");
        }

        ReadStatus readStatus = new ReadStatus(
                request.getUserId(),
                request.getChannelId(),
                Instant.now()
        );

        readStatusRepository.save(readStatus);

        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadTime()
        );
    }

    @Override
    public ReadStatusResponse findById(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id).orElseThrow(() -> new NoSuchElementException("ReadStatus를 찾을 수 없습니다."));

        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadTime()
        );
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatus -> new ReadStatusResponse(
                        readStatus.getId(),
                        readStatus.getUserId(),
                        readStatus.getChannelId(),
                        readStatus.getLastReadTime()
                )).toList();
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.getId()).orElseThrow(() -> new NoSuchElementException("ReadStatus를 찾을 수 없습니다."));

        readStatus.updateLastReadTime(request.getLastReadTime());
        readStatusRepository.save(readStatus);
        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadTime()
        );
    }

    @Override
    public void delete(UUID id) {
        if (!readStatusRepository.findById(id).isPresent()) {
            throw new NoSuchElementException("ReadStatus를 찾을 수 없습니다.");
        }
        readStatusRepository.deleteById(id);
    }
}

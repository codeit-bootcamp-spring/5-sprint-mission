package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    @Transactional
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        log.info("유저 읽음 상태 생성 시도");
        log.debug("유저 읽음 상태 생성 요청 데이터: {}", request);
        if (!userRepository.existsById(request.getUserId())
                || channelRepository.findById(request.getChannelId()).isEmpty()) {
            log.warn("유저 또는 채널이 존재하지 않음. UserId : {}, ChannelId : {}", request.getUserId(), request.getChannelId());
            throw new ReadStatusNotFoundException();
        }


        if (readStatusRepository.findByUserIdAndChannelId(request.getUserId(), request.getChannelId()).isPresent()) {
            log.info("이미 존재하는 유저 읽음 상태 업데이트 시도");

            ReadStatus existingReadStatus = readStatusRepository.findByChannelIdAndUserId(request.getChannelId(), request.getUserId());

            existingReadStatus.update(request.getLastReadAt());
            readStatusRepository.save(existingReadStatus);

            log.info("유저 읽음 상태 업데이트 성공: {}", existingReadStatus);
            return ReadStatusResponse.success(existingReadStatus);
        } else {
            log.info("새로운 유저 읽음 상태 생성 시도");
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + request.getUserId()));

            Channel channel = channelRepository.findById(request.getChannelId())
                    .orElseThrow(() -> new NoSuchElementException("Channel not found: " + request.getChannelId()));

            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);

            log.info("유저 읽음 상태 생성 성공: {}", readStatus);
            return ReadStatusResponse.success(readStatus);
        }
    }

    @Override
    @Transactional
    public ReadStatusResponse getById(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(ReadStatusNotFoundException::new);
        return ReadStatusResponse.success(readStatus);
    }

    @Override
    @Transactional
    public List<ReadStatusResponse> getAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findByUserId(userId);

        return readStatuses.stream()
                .map(ReadStatusResponse::success)
                .toList();
    }

    @Override
    @Transactional
    public List<ReadStatusResponse> getAllByChannelId(UUID channelId) {
        List<ReadStatus> readStatuses = readStatusRepository.findByUserId(channelId);

        return readStatuses.stream()
                .map(ReadStatusResponse::success)
                .toList();
    }

    @Override
    @Transactional
    public ReadStatusResponse updateById(UUID id, ReadStatusUpdateRequest request) {
        log.info("유저 읽음 상태 업데이트 시도");
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(ReadStatusNotFoundException::new);

        readStatus.update(request.getNewLastReadAt());
        readStatusRepository.save(readStatus);

        log.info("유저 읽음 상태 업데이트 성공: {}", readStatus);
        return ReadStatusResponse.success(readStatus);
    }

    @Override
    @Transactional
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
    @Transactional
    public ReadStatusResponse delete(UUID id) {
        log.info("유저 읽음 상태 삭제 시도");
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(ReadStatusNotFoundException::new);

        readStatusRepository.deleteById(id);

        log.info("유저 읽음 상태 삭제 성공: {}", readStatus);
        return ReadStatusResponse.success(readStatus);
    }
}

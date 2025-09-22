package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.readStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
        log.info("[Service] 유저 읽음 상태 생성 시도");
        log.debug("[Service] 유저 읽음 상태 생성 요청 데이터: {}", request);
        if (!userRepository.existsById(request.getUserId())
                || channelRepository.findById(request.getChannelId()).isEmpty()) {
            log.warn("[Service] 유저 또는 채널이 존재하지 않음. UserId : {}, ChannelId : {}", request.getUserId(), request.getChannelId());
            throw ReadStatusNotFoundException.withUserAndChannel(request.getUserId(), request.getChannelId());
        }


        if (readStatusRepository.findByUserIdAndChannelId(request.getUserId(), request.getChannelId()).isPresent()) {
            log.info("[Service] 이미 존재하는 유저 읽음 상태 업데이트 시도");

            ReadStatus existingReadStatus = readStatusRepository.findByChannelIdAndUserId(request.getChannelId(), request.getUserId());

            existingReadStatus.update(request.getLastReadAt());
            readStatusRepository.save(existingReadStatus);

            log.info("[Service] 유저 생성 시 유저 읽음 상태 업데이트 성공: {}", existingReadStatus);
            return ReadStatusResponse.success(existingReadStatus);
        } else {
            log.info("[Service] 새로운 유저 읽음 상태 생성 시도");
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> ReadStatusNotFoundException.withUserId(request.getUserId()));

            Channel channel = channelRepository.findById(request.getChannelId())
                    .orElseThrow(() -> ReadStatusNotFoundException.withChannelId(request.getChannelId()));

            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);

            log.info("[Service] 유저 읽음 상태 생성 성공: {}", readStatus);
            return ReadStatusResponse.success(readStatus);
        }
    }

    @Override
    @Transactional
    public ReadStatusResponse getById(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> ReadStatusNotFoundException.withReadStatusId(id));
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
        log.info("[Service] 유저 읽음 상태 업데이트 시도");
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> ReadStatusNotFoundException.withReadStatusId(id));

        readStatus.update(request.getNewLastReadAt());
        readStatusRepository.save(readStatus);

        log.info("[Service] 유저 읽음 상태 업데이트 성공: {}", readStatus);
        return ReadStatusResponse.success(readStatus);
    }

    @Override
    @Transactional
    public ReadStatusResponse updateByChannelIdAndUserId(UUID channelId, UUID userId,
                                                         ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findByChannelIdAndUserId(channelId, userId);
        if (readStatus == null) {
            throw ReadStatusNotFoundException.withUserAndChannel(userId, channelId);
        }

        readStatus.update(request.getNewLastReadAt());
        readStatusRepository.save(readStatus);

        return ReadStatusResponse.success(readStatus);
    }

    @Override
    @Transactional
    public ReadStatusResponse delete(UUID id) {
        log.info("[Service] 유저 읽음 상태 삭제 시도");
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> ReadStatusNotFoundException.withReadStatusId(id));

        readStatusRepository.deleteById(id);

        log.info("[Service] 유저 읽음 상태 삭제 성공: {}", readStatus);
        return ReadStatusResponse.success(readStatus);
    }
}

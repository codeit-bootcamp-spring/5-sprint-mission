package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("readStatusService")
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {

        // 관련된 Channel이나 User가 존재하지 않으면 예외를 발생시킵니다.
        if(userRepository.findById(request.userId()).isEmpty()){
            throw new IllegalArgumentException("user not found");
        }
        if(channelRepository.findById(request.channelId()).isEmpty()){
            throw new IllegalArgumentException("channel not found");
        }

        // 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
        if(!readStatusRepository.findAll(request.userId()).isEmpty()){
            throw new IllegalArgumentException("read status already exists");
        }
        if(!channelRepository.findById(request.channelId()).isEmpty()){
            throw new IllegalArgumentException("channel already exists");
        }

        ReadStatus readStatues = new ReadStatus(request.userId(), request.channelId());
        readStatusRepository.save(readStatues);

        return readStatues;
    }

    // id로 조회합니다
    @Override
    public Optional<ReadStatus> find(UUID userStatusId) {
        return readStatusRepository.findById(userStatusId);
    }


    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAll(userId);
    }

    @Override
    public ReadStatus update(ReadStatusUpdateRequest request) {

        ReadStatus readStatus = readStatusRepository.findById(request.userId()).
                orElseThrow(() -> new IllegalArgumentException("read staus not found"));

        readStatus.update(); // 최신 읽은 시간 업데이트
        return readStatus;
    }

    @Override
    public void delete(UUID userStatusId) {
        readStatusRepository.delete(userStatusId);
    }
}

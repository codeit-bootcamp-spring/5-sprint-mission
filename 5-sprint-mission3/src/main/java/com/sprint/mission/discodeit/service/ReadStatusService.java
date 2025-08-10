package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("readStatusService")
public class ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Autowired
    public ReadStatusService(ReadStatusRepository readStatusRepository,
                             @Qualifier("fileUserRepository") UserRepository userRepository,
                             @Qualifier("fileChannelRepository") ChannelRepository channelRepository) {
        this.readStatusRepository = readStatusRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    public ReadStatus create(ReadStatusRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new IllegalArgumentException("{"+ request.userId() + "} 사용자가 존재하지 않습니다.");
        }
        if (!channelRepository.existsById(request.channelId())) {
            throw new IllegalArgumentException("{"+ request.channelId() + "} 채널이 존재하지 않습니다.");
        }
        List<ReadStatus>  userReadStatuses = readStatusRepository.findAllByUserId(request.userId());
        userReadStatuses.forEach(v -> {
            if(v.getChannelId().equals(request.channelId())) {
                throw new IllegalArgumentException(request.userId() + "- 유저와, " + request.channelId() + "- 채널과 관련된 객체가 이미 존재합니다.");
            }
        });

        return readStatusRepository.save(new ReadStatus(request.channelId(), request.userId()));
    }

    public ReadStatus find(UUID id) {
        return readStatusRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    public ReadStatus update(ReadStatusRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.id()).orElseThrow(NoSuchElementException::new);
        readStatus.update(request.lastReadAt());
        return readStatusRepository.save(readStatus);
    }

    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }
}

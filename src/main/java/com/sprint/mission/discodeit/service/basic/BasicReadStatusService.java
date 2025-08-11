package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service("basicReadStatusService")
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Override
    public ReadStatus createStatus(ReadStatusDto.CreateReadStatus dto) {
        Channel channel = channelRepository.findById(dto.channelId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NoSuchElementException("존재하지않는 사용자입니다."));

        ReadStatus readStatus = new ReadStatus(dto.userId(), dto.channelId(), Instant.now());
        return readStatusRepository.save(readStatus);
    }
}

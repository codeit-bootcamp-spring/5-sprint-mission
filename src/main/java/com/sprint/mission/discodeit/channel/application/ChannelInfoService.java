package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.application.dto.ChannelInfoDto;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelInfoService {

    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;
    private final ReadStatusRepository readStatusRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.PUBLIC_CHANNELS)
    public List<ChannelInfoDto> findPublicChannels() {
        log.debug("[Cache Miss] find public channel infos");

        return channelRepository.findAllByType(ChannelType.PUBLIC).stream()
            .map(channelMapper::toChannelInfo)
            .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.SUBSCRIBED_CHANNELS, key = "#userId")
    public List<ChannelInfoDto> findSubscribedChannels(UUID userId) {
        log.debug("[Cache Miss] find subscribed channel infos: [userId={}]", userId);

        return readStatusRepository.findAllWithChannelByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .map(channelMapper::toChannelInfo)
            .toList();
    }
}

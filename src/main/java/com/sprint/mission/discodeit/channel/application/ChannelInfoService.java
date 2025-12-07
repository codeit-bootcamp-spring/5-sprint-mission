package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChannelInfoService {

    private final ChannelMapper channelMapper;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    @Cacheable(cacheNames = CacheName.PUBLIC_CHANNELS)
    public List<ChannelInfoDto> findAllPublicChannels() {
        log.debug("[Cache Miss] Public 채널 전체 DB 조회");

        return channelRepository.findAllByType(ChannelType.PUBLIC).stream()
            .map(channelMapper::toChannelInfo)
            .toList();
    }

    @Cacheable(cacheNames = CacheName.SUBSCRIBED_CHANNELS, key = "#userId")
    public List<ChannelInfoDto> findSubscribedChannels(UUID userId) {
        log.debug("[Cache Miss] 구독 채널 DB 조회: userId={}", userId);

        return readStatusRepository.findAllWithChannelByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .map(channelMapper::toChannelInfo)
            .toList();
    }
}

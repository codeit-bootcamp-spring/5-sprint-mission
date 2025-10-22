package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.data.ChannelDto;
import com.codeit.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.entity.ChannelType;
import com.codeit.mission.discodeit.entity.ReadStatus;
import com.codeit.mission.discodeit.mapper.ChannelMapper;
import com.codeit.mission.discodeit.repository.ChannelRepository;
import com.codeit.mission.discodeit.repository.MessageRepository;
import com.codeit.mission.discodeit.repository.ReadStatusRepository;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Transactional
    @Override
    public ChannelDto create(PublicChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();

        log.info("공개 채널 생성 요청 - name: {}, description: {}", name, description);
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);

        channelRepository.save(channel);
        log.debug("생성된 채널 상세 정보 - channelId: {}, type: {}, description: {}",
                channel.getId(), channel.getType(), description);
        return channelMapper.toDto(channel);
    }

    @Transactional
    @Override
    public ChannelDto create(PrivateChannelCreateRequest request) {
        log.info("프라이빗 채널 생성 요청 - 참가자 수: {}", request.participantIds().size());

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);

        List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds())
                .stream()
                .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
                .toList();
        readStatusRepository.saveAll(readStatuses);

        log.info("프라이빗 채널 생성 완료 - channelId: {}, 참가자 수: {}", channel.getId(),
                request.participantIds().size());
        log.debug("프라이빗 채널 ReadStatus 생성 완료 - channelId: {}, readStatus 수: {}",
                channel.getId(), readStatuses.size());
        return channelMapper.toDto(channel);
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelDto find(UUID channelId) {
        log.debug("채널 조회 요청 - channelId: {}", channelId);

        return channelRepository.findById(channelId)
                .map(channelMapper::toDto)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Channel with id " + channelId + " not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        log.debug("사용자별 채널 목록 조회 요청 - userId: {}", userId);

        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannel)
                .map(Channel::getId)
                .toList();

        return channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
                .stream()
                .map(channelMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();

        log.info("채널 수정 요청 - channelId: {}, newName: {}, newDescription: {}",
                channelId, newName, newDescription);

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Channel with id " + channelId + " not found"));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }
        channel.update(newName, newDescription);
        log.debug("수정된 채널 상세 정보 - channelId: {}, type: {}", channelId, channel.getType());
        return channelMapper.toDto(channel);
    }

    @Transactional
    @Override
    public void delete(UUID channelId) {
        log.info("채널 삭제 요청 - channelId: {}", channelId);

        if (!channelRepository.existsById(channelId)) {
            log.warn("채널 삭제 실패 - 존재하지 않는 channelId: {}", channelId);
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
        log.debug("채널 및 관련 데이터 삭제 완료 - channelId: {}", channelId);
    }
}

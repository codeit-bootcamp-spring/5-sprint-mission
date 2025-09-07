package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Service("basicChannelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Transactional
    @Override
    public ChannelResponseDto createPublicChannel(ChannelCreateRequest request) {
        Channel channel = new Channel();
        channel.setType(ChannelType.PUBLIC);
        channel.setName(request.name());
        channel.setDescription(request.description());

        Channel saved = channelRepository.save(channel);
        return channelMapper.toDto(saved);
    }

    @Transactional
    @Override
    public ChannelResponseDto createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = new Channel();
        channel.setType(ChannelType.PRIVATE);

        Channel createdChannel = channelRepository.save(channel);

        // 참여자들에 대한 읽음 상태 저장
        request.participantIds().forEach(userId -> {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

            ReadStatus readStatus = new ReadStatus();
            readStatus.setUser(user);
            readStatus.setChannel(createdChannel);
            readStatus.setLastReadAt(Instant.MIN);

            readStatusRepository.save(readStatus);
        });

        return channelMapper.toDto(createdChannel);
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelResponseDto find(UUID channelId) {
        return channelRepository.findById(channelId)
            .map(channelMapper::toDto)
            .orElseThrow(() -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(rs -> rs.getChannel().getId())
            .toList();

        return channelRepository.findAll().stream()
            .filter(channel ->
                channel.getType() == ChannelType.PUBLIC
                    || mySubscribedChannelIds.contains(channel.getId())
            )
            .map(channelMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public ChannelResponseDto update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(request.name(), request.description());
        // save() 불필요 → 변경감지 적용
        return channelMapper.toDto(channel);
    }

    @Transactional
    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        channelRepository.delete(channel); // cascade 설정시 연관 엔티티 자동 삭제
    }
}


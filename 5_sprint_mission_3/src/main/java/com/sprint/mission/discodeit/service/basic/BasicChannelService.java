package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.PrivateChannelResponseDto;
import com.sprint.mission.discodeit.dto.response.PublicChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicChannelService")
@Primary
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(ChannelRepository channelRepository, ReadStatusRepository readStatusRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.channelRepository = channelRepository;
        this.readStatusRepository = readStatusRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }


    @Override
    public Channel createPublicChannel(PublicChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channel = channelRepository.save(channel);

        for (UUID userId : request.participantUserIds()) {
            if (!userRepository.existsById(userId)) {
                throw new NoSuchElementException("존재하지 않는 사용자 ID입니다: " + userId);
            }
            ReadStatus readStatus = new ReadStatus(channel.getId(), userId);
            readStatusRepository.save(readStatus);
        }
        return channel;
    }

    @Override
    public PublicChannelResponseDto findPublicChannel(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));

        if (channel.getType() != ChannelType.PUBLIC) {
            throw new IllegalArgumentException("Public 채널이 아닙니다.");
        }

        Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channel.getId()).orElse(null);
        return new PublicChannelResponseDto(
                channel.getId(),
                channel.getName(),
                channel.getType(),
                channel.getDescription(),
                lastMessageAt
        );
    }

    @Override
    public PrivateChannelResponseDto findPrivateChannel(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널 Id가 존재하지 않습니다."));

        if (channel.getType() != ChannelType.PRIVATE) {
            throw new IllegalArgumentException("Private 채널이 아닙니다.");
        }

        List<UUID> participantIds = readStatusRepository.findAllByChannelId(channelId)
                .stream()
                .map(ReadStatus::getUserId)
                .toList();

        return new PrivateChannelResponseDto(
                channel.getId(),
                channel.getName(),
                channel.getType(),
                participantIds
        );
    }



    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<Channel> allChannels = channelRepository.findAll();

        return allChannels.stream()
                .filter(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) return true;
                    return readStatusRepository.existsByUserIdAndChannelId(userId, channel.getId());
                })
                .map(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) {
                        Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channel.getId()).orElse(null);
                        return (ChannelResponse) new PublicChannelResponseDto(
                                channel.getId(), 
                                channel.getName(), 
                                channel.getType(), 
                                channel.getDescription(),
                                lastMessageAt);
                    } else {
                        List<UUID> participantIds = readStatusRepository.findAllByChannelId(channel.getId())
                                .stream()
                                .map(ReadStatus::getUserId)
                                .toList();
                        return (ChannelResponse) new PrivateChannelResponseDto(channel.getId(), channel.getName(), channel.getType(), participantIds);
                    }
                })
                .toList();
    }

    @Override
    public Channel update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + request.channelId() + " not found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new UnsupportedOperationException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(request.newName(), request.newDescription());
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found with id " + channelId));

        messageRepository.findAllByChannelId(channelId)
                .forEach(message -> messageRepository.deleteById(message.getId()));

        readStatusRepository.findAllByChannelId(channelId)
                .forEach(readStatus -> readStatusRepository.deleteById(readStatus.getId()));

        channelRepository.deleteById(channelId);
    }
}
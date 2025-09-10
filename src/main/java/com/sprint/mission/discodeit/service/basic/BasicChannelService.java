package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Transactional
    @Override
    public Channel create(PublicChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(name, description, ChannelType.PUBLIC);

        return channelRepository.save(channel);
    }

    @Transactional
    @Override
    public Channel create(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(null, null, ChannelType.PRIVATE);

        List<UUID> participantIds = request.participantIds();
        participantIds.stream()
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
                    return new ReadStatus(user, channel, Instant.MIN);
                })
                .forEach(readStatusRepository::save);

        return channelRepository.save(channel);
    }

    @Override
    public ChannelDto find(UUID id) {
        return channelRepository.findById(id)
                .map(channelMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + id));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<Channel> mySubscribedChannels = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannel)
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || mySubscribedChannels.stream()
                                .anyMatch(subscribedChannel -> subscribedChannel.getId().equals(channel.getId())))
                .map(channelMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public Channel update(UUID id, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + id));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

        channel.update(newName, newDescription);
        return channel;
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + id));

        messageRepository.deleteAllByChannelId(id);
        readStatusRepository.deleteAllByChannelId(id);

        channelRepository.deleteById(id);
    }

}

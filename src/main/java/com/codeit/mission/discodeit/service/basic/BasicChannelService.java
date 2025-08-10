package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.channel.ChannelResponse;
import com.codeit.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.codeit.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.codeit.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.entity.ChannelType;
import com.codeit.mission.discodeit.entity.Message;
import com.codeit.mission.discodeit.entity.ReadStatus;
import com.codeit.mission.discodeit.repository.ChannelRepository;
import com.codeit.mission.discodeit.repository.MessageRepository;
import com.codeit.mission.discodeit.repository.ReadStatusRepository;
import com.codeit.mission.discodeit.service.ChannelService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("basicChannelService")
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(@Qualifier("channelRepository") ChannelRepository channelRepository,
                               @Qualifier("readStatusRepository") ReadStatusRepository readStatusRepository,
                               @Qualifier("messageRepository") MessageRepository messageRepository) {
        this.channelRepository = channelRepository;
        this.readStatusRepository = readStatusRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public ChannelResponse createPublicChannel(PublicChannelCreateRequest request) {
        Channel channel = new Channel(request.getType(), request.getName(), request.getDescription());
        Channel savedChannel = channelRepository.save(channel);

        return new ChannelResponse(savedChannel, null);
    }

    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(request.getType(), request.getName(), request.getDescription());
        Channel savedChannel = channelRepository.save(channel);

        for (UUID participantUserId : request.getParticipantUserIds()) {
            ReadStatus readStatus = new ReadStatus(participantUserId, savedChannel.getId(), Instant.now());
            readStatusRepository.save(readStatus);

        }

        return new ChannelResponse(savedChannel, null, request.getParticipantUserIds());
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found"));

        Instant lastMessageTime = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(Message::getCreatedAt)
                .orElse(null);

        List<UUID> participantUserIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participantUserIds = readStatusRepository.findAll().stream()
                    .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                    .map(ReadStatus::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
        }

        return new ChannelResponse(channel, lastMessageTime, participantUserIds);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<Channel> allChannels = channelRepository.findAll();

        return allChannels.stream()
                .filter(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) {
                        return true;
                    } else {
                        return readStatusRepository.findAll().stream()
                                .anyMatch(readStatus ->
                                        readStatus.getChannelId().equals(channel.getId()) &&
                                                readStatus.getUserId().equals(userId));
                    }
                })
                .map(channel -> {
                    Instant lastMessageTime = messageRepository.findAll().stream()
                            .filter(message -> message.getChannelId().equals(channel.getId()))
                            .max(Comparator.comparing(Message::getCreatedAt))
                            .map(Message::getCreatedAt)
                            .orElse(null);

                    List<UUID> participantUserIds = null;
                    if (channel.getType() == ChannelType.PRIVATE) {
                        participantUserIds = readStatusRepository.findAll().stream()
                                .filter(readStatus -> readStatus.getChannelId().equals(channel.getId()))
                                .map(ReadStatus::getUserId)
                                .distinct()
                                .collect(Collectors.toList());
                    }

                    return new ChannelResponse(channel, lastMessageTime, participantUserIds);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("Channel not found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("Private channels cannot be updated");
        }

        channel.update(request.getName(), request.getDescription());
        Channel savedChannel = channelRepository.save(channel);

        Instant lastMessageTime = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(request.getChannelId()))
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(Message::getCreatedAt)
                .orElse(null);
        return new ChannelResponse(savedChannel, lastMessageTime);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found");
        }

        deleteMessages(channelId);
        deleteReadStatuses(channelId);

        channelRepository.deleteById(channelId);
    }

    private void deleteMessages(UUID channelId) {
        List<Message> messages = messageRepository.findAll();
        List<Message> channelMessages = messages.stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());

        for (Message message : channelMessages) {
            messageRepository.deleteById(message.getId());
        }
    }

    private void deleteReadStatuses(UUID channelId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAll();
        List<ReadStatus> channelReadStatuses = readStatuses.stream()
                .filter(status -> status.getChannelId().equals(channelId))
                .collect(Collectors.toList());

        for (ReadStatus readStatus : channelReadStatuses) {
            readStatusRepository.deleteById(readStatus.getId());
        }
    }
}

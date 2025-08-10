package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelFindResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicChannelService")

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public BasicChannelService(ChannelRepository channelRepository,
                               MessageRepository messageRepository,
                               ReadStatusRepository readStatusRepository) {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.readStatusRepository = readStatusRepository;
    }

    @Override
    public Channel createPublicChannel(ChannelCreateRequest request) {
        if (request.type().equals(ChannelType.PUBLIC)) {
            Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
            return channelRepository.save(channel);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Channel createPrivateChannel(ChannelCreateRequest request) {
        if (request.type().equals(ChannelType.PRIVATE)) {
            Channel channel = new Channel(ChannelType.PRIVATE, null, null);
            List<UUID> members = request.members();
            for(UUID member : members){
                ReadStatus readStatus = new ReadStatus(channel.getId(), member);
                readStatusRepository.save(readStatus);
            }
            return channelRepository.save(channel);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public ChannelFindResponse find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        Instant lastReadAt = getLastReadAt(channelId);
        if (channel.getType().equals(ChannelType.PUBLIC)) {
            return ChannelFindResponse.builder().id(channelId).name(channel.getName())
                    .description(channel.getDescription()).type(ChannelType.PUBLIC)
                    .lastReadAt(lastReadAt)
                    .build();
        } else {
            List<UUID> members = new ArrayList<>();
            readStatusRepository.findAllByChannelId(channelId).forEach(v -> members.add(v.getUserId()));
            return ChannelFindResponse.builder().id(channelId).name(null).description(null)
                    .type(ChannelType.PRIVATE).lastReadAt(lastReadAt)
                    .members(members)
                    .build();
        }
    }

    @Override
    public List<ChannelFindResponse> findAll(UUID userId) {
        List<Channel> allChannels = channelRepository.findAll();
        List<ChannelFindResponse> channelList = new ArrayList<>();
        List<UUID> channelsHasUser = new ArrayList<>();

        readStatusRepository.findAllByUserId(userId).forEach(v -> channelsHasUser.add(v.getChannelId()));
        for (Channel channel : allChannels) {
            if (channel.getType().equals(ChannelType.PRIVATE) && !channelsHasUser.contains(channel.getId())) {
                allChannels.remove(channel);
            }
        }

        for (Channel channel : allChannels) {
            Instant lastReadAt = getLastReadAt(channel.getId());
            if (channel.getType().equals(ChannelType.PUBLIC)) {
                channelList.add(ChannelFindResponse.builder().id(channel.getId())
                        .name(channel.getName()).description(channel.getDescription())
                        .type(ChannelType.PUBLIC).lastReadAt(lastReadAt)
                        .build());
            } else {
                List<UUID> members = new ArrayList<>();
                readStatusRepository.findAllByChannelId(channel.getId()).forEach(v -> members.add(v.getUserId()));
                channelList.add(ChannelFindResponse.builder().id(channel.getId()).name(null).description(null)
                        .type(ChannelType.PRIVATE).lastReadAt(lastReadAt)
                        .members(members)
                        .build());
            }
        }
        return channelList;
    }

    @Override
    public Channel update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + request.channelId() + " not found"));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }
        channel.update(request.name(), request.description());
        return channelRepository.save(channel);
    }

    @Override
    public Instant getLastReadAt(UUID channelId) {
        List<Message> messages = messageRepository.findAll();

        Instant lastReadAt = null;
        for(Message message : messages){
            if(lastReadAt == null) {
                lastReadAt = message.getCreatedAt();
            } else {
                if (lastReadAt.compareTo(message.getCreatedAt()) < 0) {
                    lastReadAt = message.getCreatedAt();
                }
            }
        }
        return lastReadAt;
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        List<Message> messages = messageRepository.findAll();
        for (Message message : messages) {
            if (channelId.equals(message.getChannelId())) {
                messageRepository.deleteById(message.getId());
            }
        }
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channelId);
        for (ReadStatus readStatus : readStatuses) {
            if (channelId.equals(readStatus.getChannelId())) {
                readStatusRepository.deleteById(readStatus.getId());
            }
        }
        channelRepository.deleteById(channelId);
    }
}

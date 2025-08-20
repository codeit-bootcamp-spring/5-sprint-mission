package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel create(PublicChannelCreateRequest request) {
        String name=request.name();
        String description = request.description();

        Channel channel = new Channel(ChannelType.PUBLIC,name, description);

        return channelRepository.save(channel);
    }

    @Override
    public Channel create(PrivateChannelCreateRequest request){
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);
        request.participantIds().stream()
                .map(userId -> new ReadStatus(userId,createdChannel.getId(), Instant.MIN))
                .forEach(readStatusRepository::save);

        return createdChannel;
    }

    @Override
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(this::toDto)
                .orElseThrow(()-> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    public ChannelDto toDto(Channel channel){
        //채널의 가장 마지막 메시지 시간
        Instant lastMessageAt = messageRepository.findById(channel.getId())
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst() //Optional로 감싸기
                .orElse(Instant.MIN); // 가장 오래된 시간
        List<UUID> participantIds = new ArrayList<>();
        if(channel.getType().equals(ChannelType.PRIVATE)){
            readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .forEach(participantIds::add);
        }
        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<UUID>mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(ReadStatus::getChannelId)
                .toList();
        return channelRepository.findAll().stream()
                .filter(channel -> mySubscribedChannelIds.contains(channel.getId())
                ||channel.getType().equals(ChannelType.PUBLIC))
                .map(this::toDto)
                .toList();
    }

    @Override
    public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
                 .orElseThrow(()-> new NoSuchElementException("Channel with id " + channelId + " not found"));
        if(channel.getType().equals(ChannelType.PRIVATE)){
            throw new IllegalArgumentException("Private channels cannot be updated");
        }
        String newName = request.newName();
        String newDescription = request.newDescription();

        channel.update(newName,newDescription);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelUUID) {
        Channel channel= channelRepository.findById(channelUUID)
                .orElseThrow(()-> new NoSuchElementException("Channel with id " + channelUUID + " not found"));
        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.findAllByChannelId(channel.getId());
        channelRepository.deleteById(channelUUID);
    }
}
package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddPrivateChannelDto;
import com.sprint.mission.discodeit.dto.request.AddPublicChannelDto;
import com.sprint.mission.discodeit.dto.response.GetChannelByIdDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelService implements com.sprint.mission.discodeit.service.ChannelService {

   private final ChannelRepository channelRepository;
   private final ReadStatusRepository readStatusRepository;
   private final MessageRepository messageRepository;

    @Override
    public Channel addPublicChannel(AddPublicChannelDto addPublicChannelDto) {
        Channel channel = new Channel(
                addPublicChannelDto.channelName(),
                addPublicChannelDto.ownerUserId(),
                ChannelType.PUBLIC,
                addPublicChannelDto.channelDescription()
                );
        Optional<Channel> addedChannel = channelRepository.save(channel);
        return addedChannel.orElseThrow();
    }

    @Override
    public Channel addPrivateChannel(AddPrivateChannelDto addPrivateChannelDto) {
        Channel channel = new Channel(
                null,
                addPrivateChannelDto.ownerUserId(),
                ChannelType.PRIVATE,
                null
        );

        readStatusRepository.save(new ReadStatus(addPrivateChannelDto.ownerUserId(), channel.getId()));

        Optional<Channel> addedChannel = channelRepository.save(channel);
        return addedChannel.orElseThrow();

    }

    @Override
    public GetChannelByIdDto getChannelById(UUID channelId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow();

        List<UUID> usersIdByChannelId = null;
        if(channel.getChannelType() == ChannelType.PRIVATE){
            usersIdByChannelId = readStatusRepository.findUsersIdByChannelId(channel.getId());
        }

        List<Message> messages = messageRepository.findAllByChannelId(channelId);

        Instant recentMessageTime = null;
        for(Message message : messages){
            Instant createdAt = message.getCreatedAt();
            if(recentMessageTime == null || createdAt.isAfter(recentMessageTime)){
                recentMessageTime = createdAt;
            }
        }

        return new GetChannelByIdDto(channel, recentMessageTime, usersIdByChannelId);
    }

    @Override
    public List<Channel> getAllChannel() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID channelId, String channelName) {
        Channel channel = channelRepository.findById(channelId).orElseThrow();
        channel.updateChannelName(channelName);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        channelRepository.delete(channelId);
    }

    @Override
    public void deleteAllChannel() {
        channelRepository.deleteAll();
    }

}

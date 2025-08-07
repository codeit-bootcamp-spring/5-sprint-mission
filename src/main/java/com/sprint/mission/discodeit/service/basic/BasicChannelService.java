package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddPrivateChannelDto;
import com.sprint.mission.discodeit.dto.request.AddPublicChannelDto;
import com.sprint.mission.discodeit.dto.request.UpdateChannelDto;
import com.sprint.mission.discodeit.dto.response.GetChannelByIdDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

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

    // 왜 ReadStatus는 Private에만 추가되는거지?
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
    public List<GetChannelByIdDto> getAllChannelByUserId(UUID userId) {
        List<Channel> targetChannel = channelRepository.findPublicChannel();
        List<UUID> channelsIdByUserId = readStatusRepository.findChannelsIdByUserId(userId);
        for(UUID channelId : channelsIdByUserId){
            channelRepository.findById(channelId).ifPresent(targetChannel::add);
        }

        List<GetChannelByIdDto> resultList = new ArrayList<>();

        for(Channel channel : targetChannel){
            List<UUID> usersIdByChannelId = null;
            if(channel.getChannelType() == ChannelType.PRIVATE){
                usersIdByChannelId = readStatusRepository.findUsersIdByChannelId(channel.getId());
            }
            List<Message> messages = messageRepository.findAllByChannelId(channel.getId());

            Instant recentMessageTime = null;
            for(Message message : messages){
                Instant createdAt = message.getCreatedAt();
                if(recentMessageTime == null || createdAt.isAfter(recentMessageTime)){
                    recentMessageTime = createdAt;
                }
            }

            resultList.add(new GetChannelByIdDto(channel, recentMessageTime, usersIdByChannelId));
        }
        return resultList;
    }

    @Override
    public Channel updateChannel(UpdateChannelDto updateChannelDto) {
        Channel channel = channelRepository.findById(updateChannelDto.channelId()).orElseThrow();
        if(channel.getChannelType() == ChannelType.PRIVATE){
            throw new IllegalArgumentException("PRIVATE타입의 채널은 업데이트가 불가하다.");
        }

        channel.updateChannelName(updateChannelDto.channelName());
        channel.updateDescription(updateChannelDto.channelDescription());
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        channelRepository.delete(channelId);
        readStatusRepository.deleteByChannelId(channelId);
        messageRepository.deleteByChannelId(channelId);
    }

    @Override
    public void deleteAllChannel() {
        channelRepository.deleteAll();
    }


}

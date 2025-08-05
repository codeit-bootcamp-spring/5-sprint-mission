package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicChannelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel createPublic(PublicChannelCreateRequest request) {

        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        channelRepository.save(channel);
        return channel;
    }

    // PRIVATE 채널을 생성할 때:
    // [ ] 채널에 참여하는 User의 정보를 받아 User 별 ReadStatus 정보를 생성합니다.
    // [ ] name과 description 속성은 생략합니다.
    @Override
    public Channel createPrivate(PrivateChannelCreateRequest request) {

        Channel channel = new Channel(ChannelType.PRIVATE, null,null);
        channelRepository.save(channel);

        for (User user : request.user()) {
            ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
            readStatusRepository.save(readStatus);
        }
        return channel;
    }

    //해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
    //PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다.
    @Override
    public ChannelFindResponse find(UUID channelId) {

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(()-> new NoSuchElementException("Channel with id " + channelId + " not found"));

        Instant messageTime = messageRepository.
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        channel.update(newName, newDescription);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        channelRepository.deleteById(channelId);
    }
}

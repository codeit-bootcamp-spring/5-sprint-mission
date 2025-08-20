package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.CreateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelAccessibility;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
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
    public ChannelResponse createChannel(ChannelAccessibility accessibility, CreateChannelRequest request) {
        return switch(accessibility) {
            case PUBLIC -> handlePublic(request);
            case PRIVATE -> handlePrivate(request);
        };
    }

    private ChannelResponse handlePublic(CreateChannelRequest request) {
        Channel channel = Channel.createPublic(request.name(), request.description(), request.userIdList());
        channelRepository.save(channel);
        return toResponse(channel);
    }

    private ChannelResponse handlePrivate(CreateChannelRequest request) {
        Channel channel = Channel.createPrivate(request.userIdList());
        channelRepository.save(channel);

        for (UUID userId : channel.getUserIdList()) {
            readStatusRepository.save(new ReadStatus(userId, channel.getId(), null));
        }

        return toResponse(channel);
    }

    @Override
    public Optional<ChannelResponse> getById(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(this::toResponse);
    }

    @Override
    public List<ChannelResponse> getByChannelName(String channelName) {
        return channelRepository.findByName(channelName).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(channel -> channel.getAccessibility() == ChannelAccessibility.PUBLIC ||
                        channel.getUserIdList().contains(userId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ChannelResponse> getAll() {
        return channelRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ChannelResponse update(UpdateChannelRequest request) {
        Optional<Channel> optionalChannel = channelRepository.findById(request.id());
        if (optionalChannel.isEmpty()) return null;

        Channel channel = optionalChannel.get();
        if (channel.getAccessibility() == ChannelAccessibility.PRIVATE) return null;
        channel.update(request);

        channelRepository.save(channel);
        return toResponse(channel);
    }

    @Override
    public boolean removeById(UUID channelId) {
        if (channelRepository.findById(channelId).isEmpty()) return false;

        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);

        return channelRepository.delete(channelId);
    }

    private ChannelResponse toResponse(Channel channel) {
        return new ChannelResponse(channel.getId(), channel.getChannelName(), channel.getChannelDescription(), channel.getAccessibility(), channel.getUserIdList(), channel.getMessageList(), lastMessageAtOf(channel));
    }

    private Instant lastMessageAtOf(Channel channel) {
        return channel.getMessageList().stream()
                .map(Message::getCreateAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }
}

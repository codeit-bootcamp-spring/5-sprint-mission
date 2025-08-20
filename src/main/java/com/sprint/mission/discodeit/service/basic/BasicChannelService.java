package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel create(String name, String description) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }

        Channel channel = new Channel(name, ChannelType.PUBLIC, description);

        channelRepository.save(channel);

        return channel;
    }

    @Override
    public Channel createPrivate(List<UUID> participantIds) {

        if (participantIds == null || participantIds.isEmpty())
            throw new IllegalArgumentException("PRIVATE 채널은 최소 1명 이상의 참여자가 필요합니다.");

        // 존재하는 사용자만 허용
        List<User> members = participantIds.stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 사용자를 찾을 수 없습니다.")))
                .toList();

        Channel channel = new Channel(UUID.randomUUID().toString(),ChannelType.PRIVATE);
        channelRepository.save(channel);

        // 참여자 전원에 대해 ReadStatus 생성(lastReadAt = null)
        Instant lastReadAt = Instant.now();
        List<ReadStatus> statuses = members.stream()
                .map(user -> new ReadStatus(user.getId(),channel.getId(),lastReadAt)).toList();

        readStatusRepository.saveAll(statuses);

        return channel;
    }

    @Override
    public Channel update(UUID channelId, String name, String description) {

        // ID에 해당하는 채널 조회
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 채널을 찾을 수 없습니다."));

        if(channel.getType() != ChannelType.PUBLIC) {
            throw new IllegalStateException("PUBLIC 채널만 수정할 수 있습니다.");
        }

        channel.updateName(name != null ? name : channel.getName());
        channel.updateDescription(description != null ? description : channel.getDescription());

        channelRepository.save(channel);

        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 채널을 찾을 수 없습니다."));
    }

    @Override
    public List<Channel> findByName(String name) {
        return channelRepository.findByName(name);
    }

    @Override
    public List<ChannelDto> findByUser(UUID userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 사용자가 참여중인 모든 채널의 아이디
        List<UUID> channelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .distinct()
                .toList();

        List<Channel> channels = channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC) ||
                                (channel.getType().equals(ChannelType.PRIVATE) && channelIds.contains(channel.getId()))
                )
                .toList();

        List<UUID> participantIds = channels.stream()
                .flatMap(channel ->
                        readStatusRepository.findAllByChannelId(channel.getId()).stream()
                                .map(ReadStatus::getUserId)
                )
                .toList();



        return channels.stream()
                .map(channel -> {
                    ReadStatus readStatus = readStatusRepository.findByChannelId(channel.getId())
                            .orElse(null);

                    return ChannelDto.builder()
                            .id(channel.getId())
                            .name(channel.getName())
                            .description(channel.getDescription())
                            .type(channel.getType())
                            .participantIds(participantIds)
                            .lastMessageAt(readStatus != null ? readStatus.getLastReadAt() : null)
                            .build();
                }).toList();
    }

    @Override
    public Channel join(UUID userId, UUID channelId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 사용자을 찾을 수 없습니다."));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 채널을 찾을 수 없습니다."));

        Instant lastReadAt = Instant.now();
        ReadStatus readStatus = new ReadStatus(userId, channelId, lastReadAt);
        readStatusRepository.save(readStatus);

        return channel;
    }

    @Override
    public boolean delete(UUID id) {
        return channelRepository.delete(id);
    }
}

package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    @Override
    public ChannelDto.response create(ChannelDto.create dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }
        if (dto.type() == null) {
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }
        Channel channel = new Channel(dto.name(), dto.type());
        channelRepository.save(channel);

        return ChannelDto.response.builder()
                .id(channel.getId())
                .type(channel.getType())
                .name(channel.getName())
                .topic(channel.getTopic())
                .description(channel.getDescription())
                .createdAt(channel.getCreatedAtFormatted())
                .updatedAt(channel.getUpdatedAtFormatted())
                .build();
    }

    @Override
    public Channel createPrivate(ChannelDto.createPrivate dto) {

        if (dto.memberIds() == null || dto.memberIds().isEmpty())
            throw new IllegalArgumentException("PRIVATE 채널은 최소 1명 이상의 참여자가 필요합니다.");

        // 존재하는 사용자만 허용
        List<User> members = dto.memberIds().stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 사용자를 찾을 수 없습니다.")))
                .toList();


        // 채널 생성(이름/설명은 선택)
        Channel channel = new Channel(ChannelType.PRIVATE);
        Channel saved = channelRepository.save(channel);

        // 참여자 전원에 대해 ReadStatus 생성(lastReadAt = null)
        List<ReadStatus> statuses = members.stream()
                .map(user -> new ReadStatus(user.getId(),channel.getId())).toList();

        readStatusRepository.saveAll(statuses);

        return saved;
    }

    @Override
    public ChannelDto.response update(ChannelDto.update dto) {

        // ID에 해당하는 채널 조회
        Channel channel = channelRepository.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 채널을 찾을 수 없습니다."));

        channel.updateName(dto.name() != null ? dto.name() : channel.getName());
        channel.updateType(dto.channelType() != null ? dto.channelType() : channel.getType());
        channel.updateTopic(dto.topic() != null ? dto.topic() : channel.getTopic());
        channel.updateDescription(dto.description() != null ? dto.description() : channel.getDescription());

        channelRepository.save(channel);

        return ChannelDto.response.builder()
                .id(channel.getId())
                .type(channel.getType())
                .name(channel.getName())
                .topic(channel.getTopic())
                .description(channel.getDescription())
                .createdAt(channel.getCreatedAtFormatted())
                .updatedAt(channel.getUpdatedAtFormatted())
                .build();
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
    public boolean delete(UUID id) {
        return channelRepository.delete(id);
    }
}

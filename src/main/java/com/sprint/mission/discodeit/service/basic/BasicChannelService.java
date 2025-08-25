package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;


@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;


    @Override
    public Channel create(PublicChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);

        return channelRepository.save(channel);
    }

    @Override
    public Channel create(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        request.participantIds().stream() // 참여자 ID 목록을 순회
                .map(userId -> new ReadStatus(userId, createdChannel.getId(), Instant.MIN)) // 각 참여자에 대해 읽음상태(가장 과거 시각) 초기화
                .forEach(readStatusRepository::save); // 읽음상태를 개별 저장

        return createdChannel;  // 생성된 채널 엔티티 반환
    }


    @Override
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId) // 데이터베이스에서 channelId에 해당하는 Channel 엔티티를 찾습니다.
                .map(this::toDto) // Optional에 값이 있으면 toDto 메서드를 호출해 Channel 엔티티를 ChannelDto로 변환합니다.
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found")); // Optional이 비어있으면(채널이 없으면) NoSuchElementException을 던집니다.
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        // 1) 내가 구독(참여) 중인 채널 ID들 모으기
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream() // 유저가 속한 채널을 ReadStatus에서 가져와 채널 ID만 추출
                .map(ReadStatus::getChannelId)
                .toList();
        // 2) 모든 채널을 읽어와서
        return channelRepository.findAll().stream()
                // 3) 공개 채널이거나, 내가 구독한 채널만 필터링
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                            || mySubscribedChannelIds.contains(channel.getId())
                )
                // 4) DTO로 변환
                .map(this::toDto)
                .toList();
    }



    @Override
    public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }
        channel.update(newName, newDescription);
        return channelRepository.save(channel);
    }


    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());

        channelRepository.deleteById(channelId);
    }

    private ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId())
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);

        List<UUID> participantIds = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
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
}

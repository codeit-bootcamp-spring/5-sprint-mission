package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

// MapStruct 매퍼: Channel 엔티티를 ChannelDto로 변환하는 역할을 담당
@Mapper(componentModel = "spring", uses = {UserMapper.class}) // 스프링 빈 등록 + UserMapper 함께 사용
public abstract class ChannelMapper {

    // 메시지 관련 데이터 조회용 Repository 주입
    @Autowired
    private MessageRepository messageRepository;

    // 읽음 상태(ReadStatus) 조회용 Repository 주입
    @Autowired
    private ReadStatusRepository readStatusRepository;

    // User 엔티티 ↔ UserDto 변환 매퍼 주입
    @Autowired
    private UserMapper userMapper;

    // Channel → ChannelDto 변환 규칙 정의
    // participants, lastMessageAt 필드는 직접 작성한 메서드(resolveParticipants, resolveLastMessageAt)를 통해 매핑
    @Mapping(target = "participants", expression = "java(resolveParticipants(channel))")
    @Mapping(target = "lastMessageAt", expression = "java(resolveLastMessageAt(channel))")
    abstract public ChannelDto toDto(Channel channel);

    // 마지막 메시지가 생성된 시각을 조회
    // 메시지가 없으면 Instant.MIN(가장 작은 값) 반환
    protected Instant resolveLastMessageAt(Channel channel) {
        return messageRepository.findLastMessageAtByChannelId(
                        channel.getId())
                .orElse(Instant.MIN);
    }

    // 채널 참가자 목록을 조회하여 UserDto 리스트로 변환
    protected List<UserDto> resolveParticipants(Channel channel) {
        List<UserDto> participants = new ArrayList<>();
        // PRIVATE 타입 채널일 경우에만 참가자 조회
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelIdWithUser(channel.getId())
                    .stream()
                    // ReadStatus → User 엔티티 추출
                    .map(ReadStatus::getUser)
                    // User 엔티티 → UserDto 변환
                    .map(userMapper::toDto)
                    // 변환된 UserDto를 participants 리스트에 추가
                    .forEach(participants::add);
        }
        return participants;
    }
}


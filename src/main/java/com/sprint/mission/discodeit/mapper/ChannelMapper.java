package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Component
public class ChannelMapper {
    private final MessageRepository messageRepository; // 최근 메시지 시각 계산용

    public ChannelMapper(MessageRepository messageRepository) { // 생성자 주입
        this.messageRepository = messageRepository;             // 필드 세팅
    }

    public ChannelDto toDto(Channel e, List<UUID> participantIds) { // 엔티티+참여자IDs→DTO
        if (e == null) return null;                                // 널 가드
        Instant lastMessageAt = computeLastMessageAt(e.getId());   // 최근 메시지 시각 계산
        return new ChannelDto(                                     // record 생성
                e.getId(),                                             // id
                e.getType(),                                           // 타입
                e.getName(),                                           // 이름
                e.getDescription(),                                    // 설명
                participantIds,                                        // 참여자 ID 목록
                lastMessageAt                                          // 최근 메시지 시각
        );                                                         // 반환
    }

    private Instant computeLastMessageAt(UUID channelId) {          // 최근 메시지 시각 계산
        List<Message> ms = messageRepository.findAllByChannelId(channelId); // 채널 메시지 조회
        Instant max = null;                                         // 최대 시각 초기화
        for (Message m : ms) {                                      // 순회
            Instant created = m.getCreatedAt();                     // 생성 시각 획득
            if (created == null) continue;                          // 널이면 건너뜀
            if (max == null || created.isAfter(max)) max = created; // 최대 갱신
        }
        return max;                                                 // 결과 반환(null 가능)
    }
}

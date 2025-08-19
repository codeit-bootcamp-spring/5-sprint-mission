package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public ReadStatusDto.response updateLastReadAt(ReadStatusDto.create dto) {

        Message message = messageRepository.findById(dto.messageId())
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾지 못했습니다."));

        // 메시지 - 채널 일치하는지 확인
        if(!message.getChannelId().equals(dto.channelId())){
            throw new IllegalArgumentException("지정한 채널에 속하지 않는 메시지입니다.");
        }

        // 읽음 상태가 존재하면 없데이트, 없다면 새로 생성
        ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(dto.userId(), dto.channelId())
                .orElseGet(() -> new ReadStatus(dto.userId(), dto.channelId()));

        Instant lastReadAt = message.getCreatedAt(); // 마지막으로 읽은 시간

        // 과거로 돌아감 방지
        Instant oldReadAt = readStatus.getLastReadAt(); // null일 수도 있음

        if(oldReadAt == null || lastReadAt.isAfter(oldReadAt)){
            readStatus.updateLastReadAt(lastReadAt);
            readStatusRepository.save(readStatus);
        }
        return ReadStatusDto.response.builder()
                .userId(dto.userId())
                .channelId(dto.channelId())
                .readAt(readStatus.getLastReadAtFormatted())
                .build();
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 채널 읽기 상태가 존재하지 않습니다."));
    }

    /**
     * 사용자가 속한 모든 채널에 대해 읽지 않은 메시지가 있는지 여부를 반환
     * - 메시지의 마지막 생성 시각과 비교하여 판별
     * - 모든 채널에 대한 "읽지 않음 상태"를 반환하는 리스트
     *
     * @param userId 사용자 ID
     * @return List<ChannelUnreadDto> (채널 ID + hasUnread)
     */
    @Override
    public List<ReadStatusDto.unread> getUnreadChannels(UUID userId) {
        // 모든 채널에 대한 읽음 상태 조회
        List<ReadStatus> readStatusList = readStatusRepository.findAllByUserId(userId);

        return readStatusList.stream()
                .map(rs -> {
                    UUID channelId = rs.getChannelId(); // 채널 아이디 추출

                    // 해당 채널에서 마지막으로 생성된 메시지 시간
                    Optional<Instant> latestMessageAtOpt = messageRepository.findLastCreatedAtByChannelId(channelId);

                    // 안읽은 메시지가 있는지
                    boolean hasUnread = latestMessageAtOpt
                            .map(latest -> !rs.isRead(latest)) // 최신 메시지가 읽음 상태인지 확인
                            .orElse(false); // 없으면 읽음 처리

                    // 채널 Id와 읽음 상태를 반환
                    return new ReadStatusDto.unread(channelId, hasUnread);
                })
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 특정 채널에서 아직 읽지 않은 메시지 개수를 반환
     * - 메시지.createdAt > readStatus.lastReadAt 인 메시지 수
     *
     * @param userId    사용자 ID
     * @param channelId 채널 ID
     * @return 읽지 않은 메시지 수
     */
    @Override
    public int countUnreadMessages(UUID userId, UUID channelId) {
        Instant lastReadAt = readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .map(ReadStatus::getLastReadAt) // Optional<ReadStatus> → Optional<Instant>
                .orElse(null); // 없으면 null 반환

        if (lastReadAt == null) {
            // 한 번도 읽지 않은 경우 전체 메시지 수 리턴
            return messageRepository.findAllByChannelId(channelId).size();
        } else {
            return messageRepository.findAllByChannelIdAfter(channelId, lastReadAt).size();
        }
    }
}

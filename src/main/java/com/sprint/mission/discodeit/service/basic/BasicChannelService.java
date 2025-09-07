package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository; // 채널 CRUD를 위한 레포지토리 의존성
    private final ReadStatusRepository readStatusRepository; // 읽음 상태 CRUD 레포지토리 의존성
    private final MessageRepository messageRepository; // 메시지 CRUD 레포지토리 의존성
    private final UserRepository userRepository; // 사용자 조회를 위한 레포지토리 의존성(비공개 채널 참여자 로딩)

    @Override
    @Transactional // 생성 로직 전체 트랜잭션
    public Channel create(PublicChannelCreateRequest request) { // 공개 채널 생성 메서드
        String name = request.name(); // 요청에서 채널명 추출
        String description = request.description(); // 요청에서 설명 추출
        Channel channel = new Channel(ChannelType.PUBLIC, name, description); // 엔티티 생성(공개 채널)
        return channelRepository.save(channel); // 저장 후 엔티티 반환
    }

    @Override
    @Transactional // 생성 로직 전체 트랜잭션
    public Channel create(PrivateChannelCreateRequest request) { // 비공개 채널 생성 메서드
        Channel channel = new Channel(ChannelType.PRIVATE, null, null); // 이름/설명 없이 비공개 채널 엔티티 생성
        Channel createdChannel = channelRepository.save(channel); // 채널 선 저장(식별자/감사 필드 확정)

        Set<UUID> uniqueParticipantIds = new HashSet<>(request.participantIds()); // 요청 참여자 ID 목록에서 중복 제거
        if (uniqueParticipantIds.isEmpty()) { // 참여자가 하나도 없으면
            throw new IllegalArgumentException("At least one participant is required to create a private channel"); // 예외 발생
        }

        Instant baseTimestamp = createdChannel.getCreatedAt() != null // 채널 생성 시각을 읽음 기준으로 사용
                ? createdChannel.getCreatedAt() // Auditing으로 세팅된 생성 시각이 있으면 사용
                : Instant.now(); // 없다면 현재 시각으로 대체

        for (UUID userId : uniqueParticipantIds) { // 각 참여자 ID에 대해 반복
            User user = userRepository.findById(userId) // 사용자 엔티티 로딩
                    .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " does not exist")); // 없으면 예외
            ReadStatus status = new ReadStatus(user, createdChannel, baseTimestamp); // ReadStatus 엔티티 생성(연관 주입)
            readStatusRepository.save(status); // 저장
        }

        return createdChannel; // 생성된 채널 반환
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용 트랜잭션
    public ChannelDto find(UUID channelId) { // 채널 단건 조회 메서드
        return channelRepository.findById(channelId) // 채널 ID로 조회
                .map(this::toDto) // 있으면 DTO 변환
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found")); // 없으면 예외
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용 트랜잭션
    public List<ChannelDto> findAllByUserId(UUID userId) { // 특정 사용자가 볼 수 있는 채널 목록 조회
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId) // 사용자가 속한 ReadStatus 전부 조회
                .stream() // 스트림 변환
                .map(rs -> rs.getChannel().getId()) // 각 ReadStatus에서 채널 ID만 추출
                .toList(); // 리스트로 변환

        return channelRepository.findAll() // 전체 채널 조회
                .stream() // 스트림 변환
                .filter(channel -> // 필터링 시작
                        channel.getType() == ChannelType.PUBLIC // 공개 채널이거나
                                || mySubscribedChannelIds.contains(channel.getId()) // 내가 속한 채널이면 통과
                )
                .map(this::toDto) // DTO 변환
                .toList(); // 리스트로 반환
    }

    @Override
    @Transactional // 수정 트랜잭션
    public Channel update(UUID channelId, PublicChannelUpdateRequest request) { // 공개 채널 수정 메서드
        String newName = request.newName(); // 새 이름 추출
        String newDescription = request.newDescription(); // 새 설명 추출

        Channel channel = channelRepository.findById(channelId) // 채널 로딩
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found")); // 없으면 예외

        if (channel.getType() == ChannelType.PRIVATE) { // 비공개 채널이면
            throw new IllegalArgumentException("Private channel cannot be updated"); // 수정 불가 예외
        }

        channel.update(newName, newDescription); // 엔티티 보조 메서드로 필드 변경(전제: Channel에 해당 메서드 존재)
        return channelRepository.save(channel); // 저장 후 엔티티 반환
    }

    @Override
    @Transactional // 삭제 트랜잭션
    public void delete(UUID channelId) { // 채널 삭제 메서드
        Channel channel = channelRepository.findById(channelId) // 채널 로딩
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId)); // 없으면 예외

        messageRepository.deleteAllByChannelId(channel.getId()); // 채널의 모든 메시지 먼저 삭제
        readStatusRepository.deleteAllByChannelId(channel.getId()); // 채널의 모든 읽음 상태 삭제
        channelRepository.deleteById(channelId); // 마지막으로 채널 삭제
    }

    // 내부 변환 메서드: Channel 엔티티 -> ChannelDto
    private ChannelDto toDto(Channel channel) { // 엔티티를 외부 응답용 DTO로 변환
        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId()) // 채널의 모든 메시지 조회
                .stream() // 스트림 변환
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed()) // 생성시각 내림차순 정렬
                .map(Message::getCreatedAt) // 생성시각만 추출
                .findFirst() // 가장 최근 메시지 시각 하나 선택
                .orElse(Instant.MIN); // 없으면 Instant.MIN 사용

        List<UUID> participantIds = new ArrayList<>(); // 참가자 ID 목록 초기화
        if (channel.getType() == ChannelType.PRIVATE) { // 비공개 채널인 경우에만
            readStatusRepository.findAllByChannelId(channel.getId()) // 채널에 속한 읽음 상태들 조회
                    .stream() // 스트림 변환
                    .map(rs -> rs.getUser().getId()) // 각 ReadStatus에서 사용자 ID 추출
                    .forEach(participantIds::add); // 목록에 추가
        }

        return new ChannelDto( // DTO 생성하여 반환
                channel.getId(), // 채널 ID
                channel.getType(), // 채널 타입
                channel.getName(), // 채널 이름
                channel.getDescription(), // 채널 설명
                participantIds, // 참가자 IDs(비공개 채널일 때만 채워짐)
                lastMessageAt // 가장 최근 메시지 시각
        );
    }
}

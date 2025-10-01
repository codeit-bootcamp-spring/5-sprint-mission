package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor // Lombok: final 필드를 생성자 주입으로 초기화하는 생성자 자동 생성
@Service // 스프링 서비스 컴포넌트로 등록
public class BasicChannelService implements ChannelService { // 채널 관련 비즈니스 로직을 담당하는 서비스 구현체

    private final ChannelRepository channelRepository; // 채널 엔티티의 저장/조회 리포지토리
    //
    private final ReadStatusRepository readStatusRepository; // 채널 참여자(읽음 상태) 관리 리포지토리
    private final MessageRepository messageRepository; // 채널 내 메시지 관리 리포지토리
    private final UserRepository userRepository; // 사용자 조회를 위한 리포지토리
    private final ChannelMapper channelMapper; // Channel → ChannelDto 변환 매퍼

    @Transactional // 트랜잭션 경계: 채널 생성 작업을 하나의 트랜잭션으로 처리
    @Override // ChannelService의 오버라이드 메서드
    public ChannelDto create(PublicChannelCreateRequest request) { // 퍼블릭 채널 생성
        String name = request.name(); // 요청에서 채널명 추출
        String description = request.description(); // 요청에서 설명 추출
        Channel channel = new Channel(ChannelType.PUBLIC, name, description); // PUBLIC 타입 채널 엔티티 생성

        channelRepository.save(channel); // 채널 메타데이터 저장
        return channelMapper.toDto(channel); // 저장된 채널을 DTO로 변환하여 반환
    }

    @Transactional // 트랜잭션 경계: 비공개 채널 생성 + 참여자 묶음 저장
    @Override // ChannelService의 오버라이드 메서드
    public ChannelDto create(PrivateChannelCreateRequest request) { // 프라이빗 채널 생성
        Channel channel = new Channel(ChannelType.PRIVATE, null, null); // PRIVATE 타입 채널(이름/설명 없음)
        channelRepository.save(channel); // 채널 저장

        List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream() // 참여자 ID들로 사용자 조회
                .map(user -> new ReadStatus(user, channel, channel.getCreatedAt())) // 각 사용자에 대해 채널 참여(ReadStatus) 생성
                .toList(); // 리스트로 수집
        readStatusRepository.saveAll(readStatuses); // 참여자 읽음 상태 일괄 저장

        return channelMapper.toDto(channel); // 생성된 채널을 DTO로 반환
    }

    @Transactional(readOnly = true) // 읽기 전용 트랜잭션: 조회 성능 최적화
    @Override // ChannelService의 오버라이드 메서드
    public ChannelDto find(UUID channelId) { // 채널 단건 조회
        return channelRepository.findById(channelId) // ID로 채널 조회
                .map(channelMapper::toDto) // 존재 시 DTO로 매핑
                .orElseThrow( // 없으면 예외 발생
                        () -> new NoSuchElementException("Channel with id " + channelId + " not found")); // 예외 메시지
    }

    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    @Override // ChannelService의 오버라이드 메서드
    public List<ChannelDto> findAllByUserId(UUID userId) { // 사용자가 접근 가능한 채널 목록 조회
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream() // 사용자의 ReadStatus 목록 조회
                .map(ReadStatus::getChannel) // 각 ReadStatus에서 채널 추출
                .map(Channel::getId) // 채널 ID로 변환
                .toList(); // 리스트로 수집

        return channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds) // PUBLIC 채널 + 사용자가 구독한 채널
                .stream() // 스트림 변환
                .map(channelMapper::toDto) // DTO로 매핑
                .toList(); // 리스트 반환
    }

    @Transactional // 쓰기 트랜잭션: 채널 정보 수정
    @Override // ChannelService의 오버라이드 메서드
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) { // 퍼블릭 채널 정보 수정
        String newName = request.newName(); // 요청의 새 이름
        String newDescription = request.newDescription(); // 요청의 새 설명
        Channel channel = channelRepository.findById(channelId) // 수정 대상 채널 조회
                .orElseThrow( // 없으면 예외
                        () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        if (channel.getType().equals(ChannelType.PRIVATE)) { // 프라이빗 채널이면
            throw new IllegalArgumentException("Private channel cannot be updated"); // 수정 불가 예외 발생
        }
        channel.update(newName, newDescription); // 엔티티 상태 변경(더티 체킹으로 자동 flush)
        return channelMapper.toDto(channel); // 수정된 채널을 DTO로 반환
    }

    @Transactional // 쓰기 트랜잭션: 채널 삭제(연관 데이터 정리 포함)
    @Override // ChannelService의 오버라이드 메서드
    public void delete(UUID channelId) { // 채널 삭제
        if (!channelRepository.existsById(channelId)) { // 채널 존재 여부 선검증
            throw new NoSuchElementException("Channel with id " + channelId + " not found"); // 없으면 예외
        }

        messageRepository.deleteAllByChannelId(channelId); // 채널 내 모든 메시지 선삭제(외래키 제약 대비)
        readStatusRepository.deleteAllByChannelId(channelId); // 채널 참여자 상태 삭제

        channelRepository.deleteById(channelId); // 마지막으로 채널 자체 삭제
    }
}


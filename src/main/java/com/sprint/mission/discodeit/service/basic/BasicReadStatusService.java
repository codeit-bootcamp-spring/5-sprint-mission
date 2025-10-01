package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor // Lombok: final 필드들을 생성자 주입으로 초기화하는 생성자 자동 생성
@Service // 스프링 서비스 컴포넌트로 등록
public class BasicReadStatusService implements ReadStatusService { // 읽음 상태(ReadStatus) 도메인 로직을 담당하는 서비스 구현체

    private final ReadStatusRepository readStatusRepository; // ReadStatus 엔티티 저장/조회 리포지토리
    private final UserRepository userRepository; // 사용자 엔티티 조회 리포지토리
    private final ChannelRepository channelRepository; // 채널 엔티티 조회 리포지토리
    private final ReadStatusMapper readStatusMapper; // ReadStatus ↔ ReadStatusDto 변환 매퍼

    @Transactional // 생성 작업을 트랜잭션으로 처리
    @Override // 인터페이스 메서드 구현
    public ReadStatusDto create(ReadStatusCreateRequest request) { // ReadStatus 생성
        UUID userId = request.userId(); // 요청에서 사용자 ID 추출
        UUID channelId = request.channelId(); // 요청에서 채널 ID 추출

        User user = userRepository.findById(userId) // 사용자 조회
                .orElseThrow( // 없으면 예외
                        () -> new NoSuchElementException("User with id " + userId + " does not exist"));
        Channel channel = channelRepository.findById(channelId) // 채널 조회
                .orElseThrow( // 없으면 예외
                        () -> new NoSuchElementException("Channel with id " + channelId + " does not exist")
                );

        if (readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId())) { // 중복 존재 여부 검사
            throw new IllegalArgumentException( // 이미 존재하면 예외
                    "ReadStatus with userId " + userId + " and channelId " + channelId + " already exists");
        }

        Instant lastReadAt = request.lastReadAt(); // 마지막 읽은 시각 추출
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt); // 엔티티 생성
        readStatusRepository.save(readStatus); // 엔티티 저장

        return readStatusMapper.toDto(readStatus); // DTO로 변환하여 반환
    }

    @Override // 인터페이스 메서드 구현
    public ReadStatusDto find(UUID readStatusId) { // ReadStatus 단건 조회
        return readStatusRepository.findById(readStatusId) // ID로 조회
                .map(readStatusMapper::toDto) // 존재 시 DTO로 매핑
                .orElseThrow( // 없으면 예외
                        () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
    }

    @Override // 인터페이스 메서드 구현
    public List<ReadStatusDto> findAllByUserId(UUID userId) { // 특정 사용자 기준 모든 ReadStatus 조회
        return readStatusRepository.findAllByUserId(userId).stream() // 사용자 ID로 목록 조회
                .map(readStatusMapper::toDto) // 각 엔티티를 DTO로 변환
                .toList(); // 리스트로 수집하여 반환
    }

    @Transactional // 수정 작업을 트랜잭션으로 처리
    @Override // 인터페이스 메서드 구현
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) { // ReadStatus 수정
        Instant newLastReadAt = request.newLastReadAt(); // 새 마지막 읽은 시각 추출
        ReadStatus readStatus = readStatusRepository.findById(readStatusId) // 대상 엔티티 조회
                .orElseThrow( // 없으면 예외
                        () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
        readStatus.update(newLastReadAt); // 엔티티 상태 변경(더티 체킹으로 flush 예정)
        return readStatusMapper.toDto(readStatus); // DTO로 변환하여 반환
    }

    @Transactional // 삭제 작업을 트랜잭션으로 처리
    @Override // 인터페이스 메서드 구현
    public void delete(UUID readStatusId) { // ReadStatus 삭제
        if (!readStatusRepository.existsById(readStatusId)) { // 존재 여부 선검증
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"); // 없으면 예외
        }
        readStatusRepository.deleteById(readStatusId); // 엔티티 삭제 실행
    }
}

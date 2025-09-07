package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository; // ReadStatus CRUD를 담당하는 레포지토리 의존성 주입
    private final UserRepository userRepository; // User 존재 여부 및 조회를 위한 레포지토리 의존성 주입
    private final ChannelRepository channelRepository; // Channel 존재 여부 및 조회를 위한 레포지토리 의존성 주입

    @Override
    @Transactional // 생성 로직 전체를 하나의 트랜잭션으로 처리
    public ReadStatus create(ReadStatusCreateRequest request) { // ReadStatus 생성 메서드 시작
        UUID userId = request.userId(); // 요청에서 사용자 ID 추출
        UUID channelId = request.channelId(); // 요청에서 채널 ID 추출
        Instant lastReadAt = request.lastReadAt(); // 요청에서 마지막 읽음 시각 추출

        User user = userRepository.findById(userId) // User 엔티티 로딩(존재 확인 및 연관설정용)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " does not exist")); // 없으면 예외

        Channel channel = channelRepository.findById(channelId) // Channel 엔티티 로딩
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist")); // 없으면 예외

        boolean duplicateExists = readStatusRepository.findAllByUserId(userId) // 해당 사용자의 모든 ReadStatus 조회
                .stream() // 스트림 변환
                .anyMatch(rs -> rs.getChannel().getId().equals(channelId)); // 동일 채널에 대한 상태가 이미 있는지 검사

        if (duplicateExists) { // 중복이 존재하면
            throw new IllegalArgumentException("ReadStatus for userId " + userId + " and channelId " + channelId + " already exists"); // 예외 발생
        }

        Instant effectiveLastReadAt = lastReadAt != null ? lastReadAt : Instant.now(); // lastReadAt이 null이면 현재 시각으로 대체
        ReadStatus readStatus = new ReadStatus(user, channel, effectiveLastReadAt); // User, Channel, lastReadAt으로 ReadStatus 엔티티 생성
        return readStatusRepository.save(readStatus); // 저장 후 생성된 엔티티 반환
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용 트랜잭션 지정으로 성능 및 일관성 향상
    public ReadStatus find(UUID readStatusId) { // ReadStatus 단건 조회 메서드 시작
        return readStatusRepository.findById(readStatusId) // PK 기반 조회
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found")); // 없으면 예외
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용 트랜잭션
    public List<ReadStatus> findAllByUserId(UUID userId) { // 사용자 기준 ReadStatus 목록 조회
        return readStatusRepository.findAllByUserId(userId); // 레포지토리 메서드 결과 그대로 반환
    }

    @Override
    @Transactional // 갱신 작업 트랜잭션
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) { // ReadStatus PK 기반 갱신 메서드
        ReadStatus readStatus = readStatusRepository.findById(readStatusId) // 대상 엔티티 조회
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found")); // 없으면 예외

        Instant newLastReadAt = request.newLastReadAt(); // 새 마지막 읽음 시각 추출
        if (newLastReadAt != null) { // 값이 전달되었다면
            readStatus.mark(newLastReadAt); // 엔티티의 mark(Instant) 메서드로 갱신
        }

        return readStatusRepository.save(readStatus); // 변경 사항 저장 후 반환
    }

    @Override
    @Transactional // 삭제 트랜잭션
    public void delete(UUID readStatusId) { // ReadStatus 단건 삭제 메서드
        ReadStatus readStatus = readStatusRepository.findById(readStatusId) // 존재 여부 확인을 위해 조회
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found")); // 없으면 예외
        readStatusRepository.delete(readStatus); // 엔티티 삭제 실행
    }
}

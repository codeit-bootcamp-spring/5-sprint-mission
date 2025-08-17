package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;


    @Override
    public void create(ReadStatusCreateRequest request) {

        //1. 채널 존재 여부 확인
        Channel channel = channelRepository.findById(request.getChannelId());
        if (channel == null) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        }

        //2. 유저 존재여부 확인
        User user = userRepository.findById(request.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
        }

        //3. 유저가 어떤 채널을 읽었다는 기록(ReadStatus)을 중복 없이 저장
        List<ReadStatus> existing = readStatusRepository.findAll(); // repo에서 모든 읽음 기록 가져옴
        boolean exists = existing.stream()
                //채널 아이디와 유저 아이디가 요청값과 모두 같은 ReadStatus가 있는지 검사
                //anyMatch : 하나라도 조건에 맞으면 true 반환
                .anyMatch(rs -> rs.getChannelId().equals(request.getChannelId())
                        && rs.getUserId().equals(request.getUserId()));
        if (exists) {
            throw new IllegalArgumentException("이미 읽음 처리된 상태입니다.");
        }

        //4. 저장
        ReadStatus readStatus = new ReadStatus(
                UUID.randomUUID(),
                request.getChannelId(),
                request.getUserId(),
                Instant.now()
        );
        readStatusRepository.save(readStatus);
    }


    @Override
    public ReadStatus findById(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id);
        if (readStatus == null) {
            throw new IllegalArgumentException("해당 ID의 읽음 기록이 존재하지 않습니다.");
        }
        return readStatus;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAll().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void update(ReadStatusUpdateRequest request) {
        ReadStatus original = readStatusRepository.findById(request.getId());
        if (original == null) throw new IllegalArgumentException("수정할 ReadStatus가 존재하지 않습니다.");

        ReadStatus updated = new ReadStatus(
                original.getId(),
                original.getChannelId(),
                original.getUserId(),
                Instant.now()
        );

        readStatusRepository.save(updated);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        readStatusRepository.deleteByChannelId(channelId);
    }


    @Override
    public void delete(UUID id) {
        ReadStatus existing = readStatusRepository.findById(id);
        if (existing == null) throw new IllegalArgumentException("삭제할 ReadStatus가 존재하지 않습니다.");
        readStatusRepository.deleteById(id);
    }
}

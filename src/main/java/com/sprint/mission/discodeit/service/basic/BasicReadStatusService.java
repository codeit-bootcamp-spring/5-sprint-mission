package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;

    @Override
    public ReadStatusDto.DetailResponse create(ReadStatusDto.CreateRequest request) {
        ReadStatus readStatus = new ReadStatus(request.getUserId(), request.getChannelId());
        readStatusRepository.save(readStatus);

        return ReadStatusDto.DetailResponse.builder()
            .id(readStatus.getId())
            .channelId(readStatus.getChannelId())
            .userId(readStatus.getUserId())
            .lastReadAt(readStatus.getLastReadAt())
            .build();
    }

    @Override
    public ReadStatusDto.DetailResponse find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id).orElse(null);

        if (readStatus == null) {
            return null;
        }

        return ReadStatusDto.DetailResponse.builder()
            .id(readStatus.getId())
            .channelId(readStatus.getChannelId())
            .userId(readStatus.getUserId())
            .lastReadAt(readStatus.getLastReadAt())
            .build();
    }

    @Override
    public List<ReadStatusDto.DetailResponse> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);

        return readStatuses.stream()
            .map(rs ->
                ReadStatusDto.DetailResponse.builder()
                    .id(rs.getId())
                    .channelId(rs.getChannelId())
                    .userId(rs.getUserId())
                    .lastReadAt(rs.getLastReadAt())
                    .build())
            .toList();
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.delete(id);
    }

    @Override
    public void deleteAll() {
        readStatusRepository.deleteAll();
    }

    public ReadStatus update(UUID id) {

        ReadStatus readStatus = readStatusRepository.findById(id).orElse(null);

        if (readStatus == null) {
            return null;
        }

        readStatus.update();

        return readStatusRepository.save(readStatus);
    }
}

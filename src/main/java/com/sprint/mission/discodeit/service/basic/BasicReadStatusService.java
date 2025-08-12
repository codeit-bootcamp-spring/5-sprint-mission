package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ReadStatusDto.DetailResponse create(ReadStatusDto.CreateRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("Not Found User");
        }

        Channel channel = channelRepository.findById(request.getChannelId()).orElse(null);
        if (channel == null) {
            throw new IllegalArgumentException("Not Found Channel");
        }

        if (readStatusRepository.findAllByUserId(request.getUserId()).stream()
            .anyMatch(rs -> rs.getChannelId().equals(request.getChannelId()))) {
            throw new IllegalArgumentException("Already Registered Read Status");
        }

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

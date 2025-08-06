package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadStatusService implements com.sprint.mission.discodeit.service.ReadStatusService {

    private final ReadStatusRepository readStatusRepository;

    @Override
    public void deleteAllReadStatus() {
        readStatusRepository.deleteAll();
    }
}

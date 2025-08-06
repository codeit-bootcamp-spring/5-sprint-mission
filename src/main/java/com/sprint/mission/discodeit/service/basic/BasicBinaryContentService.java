package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public void create(BinaryContent binaryContent) {

    }

    @Override
    public BinaryContent findById(UUID id) {
        return null;
    }

    @Override
    public List<BinaryContent> findAll(List<UUID> ids) {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {

    }
}

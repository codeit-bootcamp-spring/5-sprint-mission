package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.*;

public class FileBinaryContentRepository extends FileStore<BinaryContent> implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> data = new HashMap<>();

    public FileBinaryContentRepository(String rootDir) {
        super(rootDir + "binaryContent.ser");
        Map<UUID, BinaryContent> loaded = loadFromFile();
        if (loaded != null) {
            data.putAll(loaded);
        }
    }

    // 저장
    @Override
    public void save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
        saveToFile(data);
    }

    //파일 조회
    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return List.copyOf(data.values());
    }


}

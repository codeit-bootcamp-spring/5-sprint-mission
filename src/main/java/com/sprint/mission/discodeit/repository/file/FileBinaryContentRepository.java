package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileBinaryContentRepository extends FileStore<BinaryContent> implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> data = new HashMap<>();

    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String rootDir) {
        super(Paths.get(rootDir, "binaryContent.ser").toString());
        Map<UUID, BinaryContent> loaded = loadFromFile();
        if (loaded != null) data.putAll(loaded);
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

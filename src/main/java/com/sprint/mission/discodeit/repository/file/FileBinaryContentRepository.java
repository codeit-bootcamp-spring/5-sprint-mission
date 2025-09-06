package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.common.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path BINARY_CONTENT_DIR = Path.of(BinaryContent.class.getSimpleName());

    public FileBinaryContentRepository() {
        FileUtils.init(BINARY_CONTENT_DIR);
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path path = BINARY_CONTENT_DIR.resolve(binaryContent.getId().toString());
        FileUtils.save(path, binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path path = BINARY_CONTENT_DIR.resolve(id.toString());
        return Optional.ofNullable(FileUtils.findOne(path, BinaryContent.class));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return FileUtils.findAll(BINARY_CONTENT_DIR, BinaryContent.class).stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .toList();
    }

    public List<BinaryContent> findAll() {
        return FileUtils.findAll(BINARY_CONTENT_DIR, BinaryContent.class);
    }

    @Override
    public void delete(UUID id) {
        Path path = BINARY_CONTENT_DIR.resolve(id.toString());
        FileUtils.delete(path);
    }

    public void deleteAll() {
        FileUtils.deleteAll(BINARY_CONTENT_DIR);
    }
}

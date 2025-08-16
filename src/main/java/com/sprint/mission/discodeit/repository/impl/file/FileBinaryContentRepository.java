package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Profile("dev")
public class FileBinaryContentRepository extends AbstractFileRepository<BinaryContent> implements BinaryContentRepository {

    public FileBinaryContentRepository(AppProperties appProperties) {
        super(BinaryContent.class, appProperties.storage());
    }

    @Override
    public Optional<BinaryContent> findBySha256(String sha256) {
        Objects.requireNonNull(sha256, "sha256 must not be null");
        return findAll().stream()
                .filter(b -> sha256.equalsIgnoreCase(b.getSha256()))
                .findFirst();
    }

    @Override
    public List<BinaryContent> findAllByContentType(String contentType) {
        Objects.requireNonNull(contentType, "contentType must not be null");
        return findAll().stream()
                .filter(b -> b.getContentType() != null && b.getContentType().equalsIgnoreCase(contentType))
                .toList();
    }

    @Override
    public List<BinaryContent> findAllByFilename(String filename) {
        Objects.requireNonNull(filename, "filename must not be null");
        return findAll().stream()
                .filter(b -> b.getFilename() != null && b.getFilename().equalsIgnoreCase(filename))
                .toList();
    }
}

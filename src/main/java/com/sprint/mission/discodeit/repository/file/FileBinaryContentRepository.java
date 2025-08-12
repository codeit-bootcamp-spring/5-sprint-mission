package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path directoryPath;

    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}")
            String rootDir
    ) {
        this.directoryPath = Paths.get(rootDir).toAbsolutePath().resolve("binaryContent");
    }

    @Override
    public Optional<BinaryContent> save(BinaryContent content) {
        if(content == null){
            return Optional.empty();
        }

        Path filePath = Path.of(directoryPath.toAbsolutePath() + "/" + content.getId() + FileUtil.getExtension());
        FileUtil.saveEntity(filePath, content);

        return Optional.of(content);
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        if(id == null){
            return Optional.empty();
        }

        Path path = Path.of(directoryPath.toAbsolutePath() + "/" + id + FileUtil.getExtension());
        return FileUtil.loadEntity(path, BinaryContent.class);
    }

    @Override
    public List<BinaryContent> findAll() {
        File directory = new File(directoryPath.toAbsolutePath() + "/");

        if(!directory.exists() || !directory.isDirectory()){
            return List.of();
        }

        File[] files = directory.listFiles();
        List<BinaryContent> contents = new ArrayList<>();

        if(files == null){
            return contents;
        }

        for(File file : files){
            if(file.isFile() && file.getName().endsWith(FileUtil.getExtension())){
                contents.add(FileUtil.loadEntity(file.toPath(), BinaryContent.class).orElseThrow());
            }
        }
        return contents;
    }

    @Override
    public void deleteById(UUID id) {
        if(id == null){
            return;
        }

        Path path = Path.of(directoryPath.toAbsolutePath() + "/" + id + FileUtil.getExtension());
        path.toFile().delete();
    }

    @Override
    public void deleteAll() {
        File directory = new File(directoryPath.toAbsolutePath() + "/");

        File[] files = directory.listFiles();
        if(files != null){
            for(File file : files){
                file.delete();
            }
        }
    }
}

package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path directoryPath;

    public FileReadStatusRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}")
            String rootDir
    ) {
        this.directoryPath = Paths.get(rootDir).toAbsolutePath().resolve("readStatus");
    }
    @Override
    public List<ReadStatus> findAll() {
        File directory = new File(directoryPath.toAbsolutePath() + "/");

        if(!directory.exists() || !directory.isDirectory()){
            return List.of();
        }
        File[] files = directory.listFiles();
        List<ReadStatus> readStatuses = new ArrayList<>();
        if(files == null){
            return readStatuses;
        }
        for(File file : files){
            if(file.isFile() && file.getName().endsWith(FileUtil.getExtension())){
                readStatuses.add(FileUtil.loadEntity(file.toPath(), ReadStatus.class).orElseThrow());
            }
        }
        return readStatuses;

    }

    @Override
    public Optional<ReadStatus> save(ReadStatus readStatus) {
        if(readStatus == null){
            return Optional.empty();
        }

        Path path = Path.of(directoryPath.toAbsolutePath() + "/" + readStatus.getId() + FileUtil.getExtension());
        FileUtil.saveEntity(path, readStatus);

        return Optional.of(readStatus);
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        if(readStatusId == null){
            return Optional.empty();
        }

        Path path = Path.of(directoryPath.toAbsolutePath() + "/" + readStatusId + FileUtil.getExtension());
        return FileUtil.loadEntity(path, ReadStatus.class);
    }

    @Override
    public List<UUID> findUsersIdByChannelId(UUID channelId) {
        if(channelId == null){
            return List.of();
        }

        List<UUID> returnUserUUIDs = new ArrayList<>();

        File directory = new File(directoryPath.toAbsolutePath() + "/");
        File[] files = directory.listFiles();

        if(files == null){
            return returnUserUUIDs;
        }

        for(File file : files){
            Path filePath = file.toPath();
            Optional<ReadStatus> readStatusOpt = FileUtil.loadEntity(filePath, ReadStatus.class);
            if(readStatusOpt.isPresent() && channelId.equals(readStatusOpt.get().getChannelId())){
                returnUserUUIDs.add(readStatusOpt.get().getUserId());
            }
        }

        return returnUserUUIDs;
    }

    @Override
    public List<UUID> findChannelsIdByUserId(UUID userId) {
        if(userId == null){
            return List.of();
        }

        List<UUID> returnChannelsUUIDs = new ArrayList<>();

        File directory = new File(directoryPath.toAbsolutePath() + "/");
        File[] files = directory.listFiles();

        if(files == null){
            return returnChannelsUUIDs;
        }

        for(File file : files){
            Path filePath = file.toPath();
            Optional<ReadStatus> readStatusOpt = FileUtil.loadEntity(filePath, ReadStatus.class);
            if(readStatusOpt.isPresent() && userId.equals(readStatusOpt.get().getUserId())){
                returnChannelsUUIDs.add(readStatusOpt.get().getChannelId());
            }
        }

        return returnChannelsUUIDs;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        if(userId == null){
            return List.of();
        }

        List<ReadStatus> returnReadStatuses = new ArrayList<>();

        File directory = new File(directoryPath.toAbsolutePath() + "/");
        File[] files = directory.listFiles();

        if(files == null){
            return returnReadStatuses;
        }

        for(File file : files){
            Path filePath = file.toPath();
            Optional<ReadStatus> readStatusOpt = FileUtil.loadEntity(filePath, ReadStatus.class);
            if(readStatusOpt.isPresent() && userId.equals(readStatusOpt.get().getUserId())){
                returnReadStatuses.add(readStatusOpt.get());
            }
        }

        return returnReadStatuses;
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
    public void deleteByChannelId(UUID channelId) {
        if(channelId == null){
            return;
        }

        File directory = new File(directoryPath.toAbsolutePath() + "/");
        File[] files = directory.listFiles();

        if(files == null)
            return;

        for(File file : files){
            Path filePath = file.toPath();
            Optional<ReadStatus> statusOpt = FileUtil.loadEntity(filePath, ReadStatus.class);
            if (statusOpt.isPresent() && channelId.equals(statusOpt.get().getChannelId())) {
                file.delete();
            }
        }
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

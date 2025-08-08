package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.util.FileUtil;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path directoryPath = Path.of(FileUtil.getBasePath() +"/readStatuses" );

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
        Path path = Path.of(directoryPath.toAbsolutePath() + "/" + readStatusId + FileUtil.getExtension());
        return FileUtil.loadEntity(path, ReadStatus.class);
    }

    @Override
    public List<UUID> findUsersIdByChannelId(UUID channelId) {
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
        List<UUID> returnChannelsUUIDs = new ArrayList<>();

        File directory = new File(directoryPath.toAbsolutePath() + "/");
        File[] files = directory.listFiles();

        if(files == null){
            return returnChannelsUUIDs;
        }

        for(File file : files){
            Path filePath = file.toPath();
            Optional<ReadStatus> readStatusOpt = FileUtil.loadEntity(filePath, ReadStatus.class);
            if(readStatusOpt.isPresent() && userId.equals(readStatusOpt.get().getChannelId())){
                returnChannelsUUIDs.add(readStatusOpt.get().getUserId());
            }
        }

        return returnChannelsUUIDs;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
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
        Path path = Path.of(directoryPath.toAbsolutePath() + "/" + id + FileUtil.getExtension());
        path.toFile().delete();
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        File directory = new File(directoryPath.toAbsolutePath() + "/");
        File[] files = directory.listFiles();

        if(files != null){
            for(File file : files){
                Path filePath = file.toPath();
                Optional<Message> msgOpt = FileUtil.loadEntity(filePath, Message.class);
                if (msgOpt.isPresent() && channelId.equals(msgOpt.get().getChannelId())) {
                    file.delete();
                }
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

package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.util.FileUtil;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path directoryPath = Path.of(FileUtil.getBasePath() +"/user_statuses" );

    @Override
    public Optional<UserStatus> save(UserStatus userStatus) {
        if(userStatus == null){
            return Optional.empty();
        }

        Path path = Path.of(directoryPath.toAbsolutePath() + "/" + userStatus.getId() + FileUtil.getExtension());
        FileUtil.saveEntity(path, userStatus);

        return Optional.of(userStatus);
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        Path path = Path.of(directoryPath.toAbsolutePath() + "/" + userStatusId + FileUtil.getExtension());
        return FileUtil.loadEntity(path, UserStatus.class);
    }

    @Override
    public List<UserStatus> findAll() {
        File directory = new File(directoryPath.toAbsolutePath() + "/");

        if(!directory.exists() || !directory.isDirectory()){
            return List.of();
        }

        File[] files = directory.listFiles();
        List<UserStatus> status = new ArrayList<>();

        if(files == null){
            return status;
        }

        for(File file : files){
            if(file.isFile() && file.getName().endsWith(FileUtil.getExtension())){
                status.add(FileUtil.loadEntity(file.toPath(), UserStatus.class).orElseThrow());
            }
        }
        return status;
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        File directory = new File(directoryPath.toAbsolutePath() + "/");
        File[] files = directory.listFiles();

        if(files == null){
            return Optional.empty();
        }

        for(File file : files){
            Path filePath = file.toPath();
            Optional<UserStatus> userStatusOpt = FileUtil.loadEntity(filePath, UserStatus.class);
            if(userStatusOpt.isPresent() && userId.equals(userStatusOpt.get().getUserId())){
                return userStatusOpt;
            }
        }

        return Optional.empty();
    }

    @Override
    public void deleteById(UUID id) {
        Path path = Path.of(directoryPath.toAbsolutePath() + "/" + id + FileUtil.getExtension());

        path.toFile().delete();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        File directory = new File(directoryPath.toAbsolutePath() + "/");
        File[] files = directory.listFiles();

        if(files == null){
            throw new IllegalArgumentException("FileUserStatusRepository: No files found in directory");
        }

        for(File file : files){
            Path filePath = file.toPath();
            Optional<UserStatus> userStatusOpt = FileUtil.loadEntity(filePath, UserStatus.class);
            if(userStatusOpt.isPresent() && userId.equals(userStatusOpt.get().getUserId())){
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

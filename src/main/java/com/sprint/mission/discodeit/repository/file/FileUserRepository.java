package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.util.FileUtil;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {

    private static final Path directoryPath = Path.of(FileUtil.getBasePath() +"/users");

    @Override
    public Optional<User> save(User user){
        if(user == null){
            return Optional.empty();
        }

        Path filePath = Path.of(directoryPath.toAbsolutePath() + "/" + user.getId() + FileUtil.getExtension());
        FileUtil.saveEntity(filePath, user);

        return Optional.of(user);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        Path filePath = Path.of(directoryPath.toAbsolutePath() + "/" + userId + FileUtil.getExtension());
        return FileUtil.loadEntity(filePath, User.class);
    }

    @Override
    public List<User> findAll() {
        File directory = new File(directoryPath.toAbsolutePath() + "/");

        if(!directory.exists() || !directory.isDirectory()){
            return List.of();
        }
        File[] files = directory.listFiles();
        List<User> users = new ArrayList<>();
        if(files == null){
            return users;
        }
        for(File file : files){
            if(file.isFile() && file.getName().endsWith(FileUtil.getExtension())){
                users.add(FileUtil.loadEntity(file.toPath(), User.class).orElseThrow());
            }
        }
        return users;
    }

    @Override
    public void delete(UUID userId) {
        Path path = Path.of(directoryPath.toAbsolutePath() + "/" + userId + FileUtil.getExtension());

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

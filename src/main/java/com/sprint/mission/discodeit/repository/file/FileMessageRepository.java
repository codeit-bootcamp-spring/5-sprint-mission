package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.util.FileUtil;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private final Path directoryPath = Path.of(FileUtil.getBasePath() +"/messages");

    @Override
    public Optional<Message> save(Message message) {
        if(message == null){
            return Optional.empty();
        }

        Path filePath = Path.of(directoryPath.toAbsolutePath() + "/" + message.getId() + FileUtil.getExtension());
        FileUtil.saveEntity(filePath, message);
        return Optional.of(message);
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        Path filePath = Path.of(directoryPath.toAbsolutePath() + "/" + messageId + FileUtil.getExtension());
        return FileUtil.loadEntity(filePath, Message.class);
    }

    @Override
    public List<Message> findAll() {
        File directory = new File(directoryPath.toAbsolutePath() + "/");

        if(!directory.exists() || !directory.isDirectory()){
            return List.of();
        }

        File[] files = directory.listFiles();
        ArrayList<Message> messages = new ArrayList<>();

        if(files == null){
            return messages ;
        }

        for(File file : files){
            if(file.isFile() && file.getName().endsWith(FileUtil.getExtension())){
                messages.add(FileUtil.loadEntity(file.toPath(), Message.class).orElseThrow());
            }
        }
        return messages;
    }

    @Override
    public void delete(UUID messageId) {
        Path path = Path.of(directoryPath.toAbsolutePath() + "/" + messageId + FileUtil.getExtension());

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

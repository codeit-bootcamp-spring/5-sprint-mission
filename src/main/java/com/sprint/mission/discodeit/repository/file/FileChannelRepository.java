package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.util.FileUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private final Path directoryPath = Path.of(FileUtil.getBasePath() +"/channels" );

    @Override
    public Optional<Channel> save(Channel channel) {
        Path filePath = Path.of(directoryPath.toAbsolutePath() + "/" + channel.getId() + FileUtil.getExtension());
        FileUtil.saveEntity(filePath, channel);

        return Optional.of(channel);
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        Path filePath = Path.of(directoryPath.toAbsolutePath() + "/" + channelId + FileUtil.getExtension());
        return FileUtil.loadEntity(filePath, Channel.class);
    }

    @Override
    public List<Channel> findAll() {
        File directory = new File(directoryPath.toAbsolutePath() + "/");

        if(!directory.exists() || !directory.isDirectory()){
            return List.of();
        }

        File[] files = directory.listFiles();
        List<Channel> channels = new ArrayList<>();

        if(files == null){
            return channels;
        }
        for(File file : files){
            if(file.isFile() && file.getName().endsWith(FileUtil.getExtension())){
                channels.add(FileUtil.loadEntity(file.toPath(), Channel.class).orElseThrow());
            }
        }
        return channels;
    }

    @Override
    public void delete(Channel channel) {
        Path path = Path.of(directoryPath.toAbsolutePath() + "/" + channel.getId() + FileUtil.getExtension());
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

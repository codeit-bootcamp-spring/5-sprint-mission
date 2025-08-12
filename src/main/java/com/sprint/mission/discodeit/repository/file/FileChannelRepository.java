package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.configuration.RepositoryProps;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.ThrowableIOException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private final Map<UUID, Channel> channelMap = new HashMap<>();

    public FileChannelRepository(RepositoryProps props) {
        Path root = Paths.get(props.getFileDirectory());
        if (!root.isAbsolute()) {
            root = Paths.get(System.getProperty("user.dir")).resolve(root);
        }
        this.DIRECTORY = root.resolve(Channel.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new ThrowableIOException("디렉토리 생성 실패 : " + DIRECTORY, e);
            }
        }
        load();
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    private void load() {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            List<Channel> list = paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new ThrowableIOException("불러오기 실패 : " + path, e);
                        }
                    })
                    .toList();
            list.forEach(channel -> channelMap.put(channel.getId(), channel));
        } catch (IOException e) {
            throw new ThrowableIOException("불러오기 실패 : " + DIRECTORY, e);
        }
    }

    @Override
    public Channel save(Channel channel) {
        Path path = resolvePath(channel.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
            channelMap.put(channel.getId(), channel);
        } catch (IOException e) {
            throw new ThrowableIOException("저장 실패 : " + path, e);
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(channelMap.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(channelMap.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return channelMap.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
            channelMap.remove(id);
        } catch (IOException e) {
            throw new ThrowableIOException("삭제 실패 : " + path, e);
        }
    }
}

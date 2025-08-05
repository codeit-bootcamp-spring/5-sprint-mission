package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileChannelRepository implements ChannelRepository {
    private final Path channelDirectory;

    public FileChannelRepository() throws IOException {
        this.channelDirectory = Path.of("data", "channels");
        if (!Files.exists(channelDirectory)) {
            Files.createDirectories(channelDirectory);
        }
    }

    private Path getChannelFile(UUID id) {
        return Path.of(channelDirectory.toString(), id.toString() + ".ser");
    }

    @Override
    public void save(Channel channel) throws IOException {
        FileOutputStream fos = new FileOutputStream(getChannelFile(channel.getId()).toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(channel);
        oos.close();
        fos.close();
    }

    @Override
    public Channel findById(UUID id) throws IOException, ClassNotFoundException {
        Path filePath = getChannelFile(id);
        if (!Files.exists(filePath)) {
            return null;
        }

        FileInputStream fis = new FileInputStream(filePath.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis);
        Channel channel = (Channel) ois.readObject();

        ois.close();
        fis.close();

        return channel;
    }

    @Override
    public Channel findByName(String name) throws IOException, ClassNotFoundException {
        try (Stream<Path> paths = Files.list(channelDirectory)) {
            for (Path path : paths.toList()) {
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis);

                Channel channel = (Channel) ois.readObject();
                if (channel.getName().equals(name)) {
                    return channel;
                }

                ois.close();
                fis.close();
            }
        }
        return null;
    }


    @Override
    public List<Channel> findAll() throws IOException, ClassNotFoundException {
        List<Channel> result = new ArrayList<>();
        try (Stream<Path> paths = Files.list(channelDirectory)) {
            for (Path path : paths.toList()) {
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis);
                result.add((Channel) ois.readObject());

                ois.close();
                fis.close();
            }
        }
        return result;
    }

    @Override
    public void update(Channel channel) throws IOException {
        save(channel);
    }

    @Override
    public void delete(UUID id) throws IOException {
        Files.deleteIfExists(getChannelFile(id));
    }
}

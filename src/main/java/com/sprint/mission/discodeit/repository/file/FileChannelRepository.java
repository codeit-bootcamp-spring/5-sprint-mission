package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final Path directory = Path.of("Channel");

    public FileChannelRepository() throws IOException {
        try {
            Files.createDirectory(directory);
        } catch (FileAlreadyExistsException e) {
            System.out.println(directory + " Directory already exists!");
        }
    }

    @Override
    public Channel save(Channel channel) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Channel/" + channel.getId().toString()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Channel/" + id.toString()))) {
            Channel channel = (Channel) ois.readObject();
            return Optional.of(channel);
        } catch (FileNotFoundException e) {
            return Optional.empty();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();
        File[] files = directory.toFile().listFiles();

        for (File file : files) {
            if (file.isFile()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()))) {
                    Channel channel = (Channel) ois.readObject();
                    channels.add(channel);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return channels;
    }

    @Override
    public Channel update(UUID id, Channel channel) {
        Channel ch;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Channel/" + id.toString()))) {
            ch = (Channel) ois.readObject();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ch.setName(channel.getName());
        ch.setDescription(channel.getDescription());
        ch.setChannelType(channel.getChannelType());

        save(ch);
        return channel;
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = Path.of("Channel/" + id.toString());
        if (Files.exists(path)) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteById(UUID id) {
        Path path = Path.of("Channel/" + id.toString());
        try {
            Files.delete(path);
            System.out.println("삭제 성공");
        } catch (NoSuchFileException e) {
            System.out.println("삭제 실패");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

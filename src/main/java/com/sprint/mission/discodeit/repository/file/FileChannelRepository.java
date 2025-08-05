package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final File file = new File("channels.ser");

    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> data = readFile();
        data.put(channel.getId(), channel);
        writeFile(data);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return readFile().get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(readFile().values());
    }

    @Override
    public Channel update(UUID id, Channel updatedChannel) {
        Map<UUID, Channel> data = readFile();
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("Channel not found: " + id);
        }
        data.put(id, updatedChannel);
        writeFile(data);
        return updatedChannel;
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Channel> data = readFile();
        data.remove(id);
        writeFile(data);
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> readFile() {
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            return (Map<UUID, Channel>) obj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void writeFile(Map<UUID, Channel> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


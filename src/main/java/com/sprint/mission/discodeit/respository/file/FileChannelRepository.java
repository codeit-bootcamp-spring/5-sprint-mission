package com.sprint.mission.discodeit.respository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.respository.ChannelRepository;
import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final String FILE_PATH = "data/channel.store";
    private Map<UUID, Channel> channelMap = new HashMap<>();

    public FileChannelRepository() {
        loadFromFile();
    }

    @Override
    public Channel save(Channel channel) {
        channelMap.put(channel.getId(), channel);
        saveToFile();
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return channelMap.get(id);
    }

    @Override
    public List<Channel> findByName(String name) {
        List<Channel> result = new ArrayList<>();
        for (Channel channel : channelMap.values()) {
            if (channel.getName().equals(name)) {
                result.add(channel);
            }
        }
        return result;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public Channel updateName(UUID id, String name) {
        Channel channel = channelMap.get(id);
        if (channel != null) {
            channel.updateName(name);
            saveToFile();
        }
        return channel;
    }

    @Override
    public Channel updateTopic(UUID id, String topic) {
        Channel channel = channelMap.get(id);
        if (channel != null) {
            channel.updateTopic(topic);
            saveToFile();
        }
        return channel;
    }

    @Override
    public void deleteById(UUID id) {
        channelMap.remove(id);
        saveToFile();
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channelMap);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                channelMap = (Map<UUID, Channel>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("채널 파일 불러오기 실패: " + e.getMessage());
        }
    }
}

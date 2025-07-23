package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    private final String FILE_PATH = "data/channel.store";
    private Map<UUID, Channel> channelMap = new HashMap<>();

    public FileChannelService() {
        loadFromFile();
    }

    @Override
    public Channel create(String name, String topic) {
        Channel channel = new Channel(name, topic); // 생성자 정의에 따라 수정
        channelMap.put(channel.getId(), channel);
        saveToFile();
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(channelMap.get(id));
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
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs(); // 디렉토리 없으면 생성
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
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
            System.out.println("채널 불러오기 실패, 빈 상태로 초기화");
            channelMap = new HashMap<>();
        }
    }

}

package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileMessageService implements MessageService {


    private static final String FILE_PATH =
            System.getProperty("user.home") + "/Desktop/messages.txt";

    private final Map<UUID, User> userData;
    private final Map<UUID, Channel> channelData;
    private final Map<String, User> userByName;

    public FileMessageService(Map<UUID, User> userData, Map<UUID, Channel> channelData) {
        this.userData = userData;
        this.channelData = channelData;
        this.userByName = new HashMap<>();
        for (User user : userData.values()) {
            userByName.put(user.getUserName(), user);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Message> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(Message message) {
        if (!userByName.containsKey(message.getSender())) {
            throw new IllegalArgumentException("존재하지 않는 보낸 사람입니다: " + message.getSender());
        }

        if (!userByName.containsKey(message.getReceiver())) {
            throw new IllegalArgumentException("존재하지 않는 받는 사람입니다: " + message.getReceiver());
        }

        Map<UUID, Message> data = loadData();
        data.put(message.getId(), message);
        saveData(data);
    }

    @Override
    public Message find(UUID id) {
        Map<UUID, Message> data = loadData();
        return data.get(id);
    }

    @Override
    public ArrayList<Message> allFind() {
        Map<UUID, Message> data = loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, Message message) {
        Map<UUID, Message> data = loadData();
        if (data.containsKey(id)) {
            data.put(id, message);
            saveData(data);
        }
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Message> data = loadData();
        if (data.remove(id) != null) {
            saveData(data);
        }
    }
}

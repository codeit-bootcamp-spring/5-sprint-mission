package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private static final String FILE_PATH = System.getProperty("user.home") + "/Desktop/message.txt";


    @Override
    public void save(Map<UUID, Message> data) {


        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<UUID, Message> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH));
            return (Map<UUID, Message>) ois.readObject();

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }

    }

    @Override
    public void clear() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }

    }
}

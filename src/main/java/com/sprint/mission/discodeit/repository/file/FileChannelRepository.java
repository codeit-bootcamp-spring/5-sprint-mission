package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private static final String FILE_PATH = System.getProperty("user.home") + "/Desktop/channel.txt";


    @Override
    public void save(Map<UUID, Channel> data) {

        try {

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
            oos.writeObject(data);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<UUID, Channel> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try {

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH));
            return (Map<UUID, Channel>) ois.readObject();

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

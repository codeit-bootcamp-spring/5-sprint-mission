package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private static final String FILE_PATH =
            System.getProperty("user.home") + "/Desktop/channels.txt";


    private Map<UUID, Channel> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try {
            ObjectInputStream oip = new ObjectInputStream(new FileInputStream(file));
            Object obj = oip.readObject();
            if (obj instanceof Map) {
                return (Map<UUID, Channel>) obj;
            }



        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }


        return new HashMap<>();

    }

    private void saveData(Map<UUID, Channel> data) {

        try {

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
            oos.writeObject(data);

        } catch (Exception e) {
         e.printStackTrace();
        }

    }





    @Override
    public void create(Channel channel) {

        Map<UUID, Channel> data = loadData();
        data.put(channel.getId(), channel);
        saveData(data);

    }

    @Override
    public Channel find(UUID id) {
        Map<UUID, Channel> data = loadData();
        return data.get(id);
    }

    @Override
    public ArrayList<Channel> allFind() {
        Map<UUID, Channel> data = loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, Channel channel) {
        Map<UUID, Channel> data = loadData();
            if (data.containsKey(id)) {
                data.put(id,channel);
                saveData(data);
            }
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Channel> data = loadData();
        if (data.remove(id) != null) {
            saveData(data);
        }
    }
}

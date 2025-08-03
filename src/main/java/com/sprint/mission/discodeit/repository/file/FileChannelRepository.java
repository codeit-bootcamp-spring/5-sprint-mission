package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final String DIRECTORY;
    private final String EXTENSION;

    public FileChannelRepository(){
        DIRECTORY = "CHANNEL";
        EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if(!path.toFile().exists()){
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Channel save(Channel channel) {
        Path path = new File(DIRECTORY,channel.getId()+EXTENSION).toPath();
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(channel);
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Path path = Paths.get(DIRECTORY,id+EXTENSION);
        Optional<Channel> channel = Optional.empty();
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            channel = Optional.of((Channel)ois.readObject());
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channelList = new ArrayList<>();
        File file = new File(DIRECTORY);
        File[] folder = file.listFiles((dir,name)->name.endsWith(EXTENSION));
        if(folder==null){return channelList;}
        for(File f:folder) {
            try (FileInputStream fis = new FileInputStream(f);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                channelList.add((Channel)ois.readObject());

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return channelList;
    }

    @Override
    public long count() {
        File folder = new File(DIRECTORY);
        File[] file = folder.listFiles((dir,name)-> name.endsWith(EXTENSION));
        if(file== null) return 0;

        return file.length;
    }

    @Override
    public boolean delete(UUID id) {
        File file = new File(DIRECTORY,id+EXTENSION);
        return file.delete();
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = Paths.get(DIRECTORY,id+EXTENSION);
        return Files.exists(path);
    }

    @Override
    public boolean update(UUID channelUUID, String channelname, String description) {
        File file = new File(DIRECTORY, channelUUID + EXTENSION);
        Channel channel;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            channel = (Channel)ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        channel.update(channelname, description);
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos);){
            oos.writeObject(channel);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}

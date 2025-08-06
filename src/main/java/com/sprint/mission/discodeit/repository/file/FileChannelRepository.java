package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelRepository implements ChannelRepository {

    private final String DIRECTORY;
    private final String EXTENSION;

    public FileChannelRepository() {
        this.DIRECTORY = "CHANNEL";
        this.EXTENSION = ".ser";

        Path path = Paths.get(DIRECTORY);
        if(!path.toFile().exists()) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Channel save(Channel channel) {

        Path path = Paths.get(DIRECTORY, channel.getId() + EXTENSION);

        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channel = null;
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)){
            channel = (Channel)ois.readObject();
        } catch (IOException | ClassNotFoundException e){
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(channel);
    }

    @Override
    public List<Channel> findAll() {
        Path directory = Paths.get(DIRECTORY);

        if(Files.exists(directory)) {
            try{
                List<Channel> channels = Files.list(directory)
                        .map(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ){
                                Object data = ois.readObject();
                                return (Channel) data;
                            } catch(IOException | ClassNotFoundException e){
                                throw new RuntimeException(e);
                            }
                        }).toList();
                return channels;
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }else {
            return new ArrayList<>();
        }
    }


    @Override
    public Channel delete(UUID id) {
        Channel channel;
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            channel = (Channel)ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public boolean existById(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        return Files.exists(path);
    }
}

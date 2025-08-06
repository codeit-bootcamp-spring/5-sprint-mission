package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileChannelRepository implements ChannelRepository {

    private final Path directory = Paths.get(System.getProperty("user.dir"),"data", "channels");

    public FileChannelRepository() {
        createDirectory();
    }

    private void createDirectory() {
        try{
         Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패" + directory, e);
        }
    }

    private Path getFilePath(UUID id){
        return directory.resolve(id.toString().concat(".ser"));
    }

    @Override
    public Channel save(Channel channel) {
        Path file = getFilePath(channel.getId());
        try (FileOutputStream fos = new FileOutputStream(file.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(channel);
            return channel;
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 실패 : " + file , e);
        }
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Path file = getFilePath(id);
        if(Files.notExists(file)){
            return Optional.empty();
        }

        try (FileInputStream fis = new FileInputStream(file.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            Channel channel = (Channel) ois.readObject();
            if(channel != null){
                return Optional.of(channel);
            } else {
                return Optional.empty();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 불러오기 실패 : " + file, e);
        }
    }

    @Override
    public List<Channel> findAll() {

        try{
         return Files.list(directory).filter(path -> path.toString().endsWith(".ser")).map(path ->{
             try (FileInputStream fis = new FileInputStream(path.toFile());
                  ObjectInputStream ois = new ObjectInputStream(fis)) {
              return (Channel) ois.readObject();
             } catch (IOException | ClassNotFoundException e) {
                 throw new RuntimeException("채널 로딩 실패: " + path, e);
             }
         }).collect(Collectors.toList());
        } catch (IOException e) {
           return new ArrayList<>();
        }
    }

    @Override
    public Channel update(Channel channel) {
        return save(channel);
    }

    @Override
    public Channel delete(UUID id) {
        Optional<Channel> optionalChannel = findById(id);
        if (optionalChannel.isEmpty()) return null;
        try {
            Files.deleteIfExists(getFilePath(id));
        } catch (IOException e) {
            throw new RuntimeException("채널 삭제 실패: " + id, e);
        }
        return optionalChannel.get();
    }
}

//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//
//import java.io.IOException;
//import java.util.*;
//
//public class FileChannelService implements ChannelService {
//    private static final FileChannelService instance;
//
//    static {
//        try {
//            instance = new FileChannelService();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private final FileChannelRepository repository;
//
//    private FileChannelService() throws IOException {
//        this.repository = new FileChannelRepository();
//    }
//
//    public static FileChannelService getInstance() {
//        return instance;
//    }
//
//    @Override
//    public void create(Channel channel) throws IOException {
//        repository.save(channel);
//    }
//
//    @Override
//    public Channel get(UUID id) throws IOException, ClassNotFoundException {
//        return repository.findById(id);
//    }
//
//    @Override
//    public Channel get(String name) throws IOException, ClassNotFoundException {
//        return repository.findByName(name);
//    }
//
//    @Override
//    public List<Channel> getAll() throws IOException, ClassNotFoundException {
//        return repository.findAll();
//    }
//
//    @Override
//    public void update(Channel channel) throws IOException {
//        repository.update(channel);
//    }
//
//    @Override
//    public void delete(UUID id) throws IOException {
//        repository.delete(id);
//    }
//}

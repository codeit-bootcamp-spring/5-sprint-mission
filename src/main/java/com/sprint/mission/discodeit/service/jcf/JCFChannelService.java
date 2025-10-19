//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//
//import java.util.*;
//
//public class JCFChannelService implements ChannelService {
//    private static final JCFChannelService instance = new JCFChannelService();
//    private final JCFChannelRepository repository;
//
//    private JCFChannelService() {
//        this.repository = new JCFChannelRepository();
//    }
//
//    public static JCFChannelService getInstance() {
//        return instance;
//    }
//
//    @Override
//    public void create(Channel channel) {
//        repository.save(channel);
//    }
//
//    @Override
//    public Channel get(UUID id) {
//        return repository.findById(id);
//    }
//
//    @Override
//    public Channel get(String name) {
//        return repository.findByName(name);
//    }
//
//    @Override
//    public List<Channel> getAll() {
//        return repository.findAll();
//    }
//
//    @Override
//    public void update(Channel channel) {
//        repository.update(channel);
//    }
//
//    @Override
//    public void delete(UUID id) {
//        repository.delete(id);
//    }
//}

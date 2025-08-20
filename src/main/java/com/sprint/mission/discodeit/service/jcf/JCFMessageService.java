package com.sprint.mission.discodeit.service.jcf;//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
//import com.sprint.mission.discodeit.service.MessageService;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//
//public final class JCFMessageService implements MessageService {
//    private static final JCFMessageService instance = new JCFMessageService();
//
//    private final MessageRepository repository;
//
//    private JCFMessageService() {
//        this.repository = new JCFMessageRepository();
//    }
//
//    public static JCFMessageService getInstance() {
//        return instance;
//    }
//
//    @Override
//    public void create(Message message) throws IOException {
//        repository.save(message);
//    }
//
//    @Override
//    public Message get(UUID id) throws IOException, ClassNotFoundException {
//        return repository.findById(id);
//    }
//
//    @Override
//    public Message get(String content) throws IOException, ClassNotFoundException {
//        return repository.findByContent(content);
//    }
//
//    @Override
//    public List<Message> getAll() throws IOException, ClassNotFoundException {
//        return repository.findAll();
//    }
//
//    @Override
//    public void update(Message message) throws IOException {
//        repository.update(message);
//    }
//
//    @Override
//    public void delete(UUID id) throws IOException {
//        repository.delete(id);
//    }
//}

package com.sprint.mission.discodeit.service.file;//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
//import com.sprint.mission.discodeit.service.MessageService;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//
//public final class FileMessageService implements MessageService {
//
//    private static final FileMessageService singleton;
//
//    static {
//        try {
//            singleton = new FileMessageService();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static FileMessageService getInstance() {
//        return singleton;
//    }
//
//    private final MessageRepository repository;
//
//    private FileMessageService() throws IOException {
//        this.repository = new FileMessageRepository();
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

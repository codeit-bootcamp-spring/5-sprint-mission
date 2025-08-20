package com.sprint.mission.discodeit.service.file;//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.file.FileUserRepository;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//
//public class FileUserService implements UserService {
//    private static final FileUserService singleton;
//
//    static {
//        try {
//            singleton = new FileUserService();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private final UserRepository userRepository;
//
//    private FileUserService() throws IOException {
//        this.userRepository = new FileUserRepository();  // FileUserRepository 의존
//    }
//
//    public static FileUserService getInstance() {
//        return singleton;
//    }
//
//    @Override
//    public void create(User user) throws IOException {
//        userRepository.save(user);
//    }
//
//    @Override
//    public User get(UUID id) throws IOException, ClassNotFoundException {
//        return userRepository.findById(id);
//    }
//
//    @Override
//    public User get(String name) throws IOException, ClassNotFoundException {
//        return userRepository.findByName(name);
//    }
//
//    @Override
//    public List<User> getAll() throws IOException, ClassNotFoundException {
//        return userRepository.findAll();
//    }
//
//    @Override
//    public void update(User user) throws IOException {
//        userRepository.update(user);
//    }
//
//    @Override
//    public void delete(UUID id) throws IOException {
//        userRepository.delete(id);
//    }
//}

package com.sprint.mission.discodeit.service.jcf;//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//
//public class JCFUserService implements UserService {
//    private static final JCFUserService instance = new JCFUserService();
//    private final UserRepository userRepository;
//
//    private JCFUserService() {
//        this.userRepository = new JCFUserRepository();
//    }
//
//    public static JCFUserService getInstance() {
//        return instance;
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

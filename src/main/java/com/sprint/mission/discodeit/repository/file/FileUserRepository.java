package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FileUserRepository extends AbstractFileRepository<User> implements UserRepository {

    public FileUserRepository() {
        super("users");
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return dataMap.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return dataMap.values().stream()
                .filter(user -> user.getUserName().equals(userName))
                .findFirst();
    }

    @Override
    public List<User> findByNickName(String nickname) {
        return dataMap.values().stream()
                .filter(user -> user.getNickname().equals(nickname))
                .collect(Collectors.toList());
    }
}

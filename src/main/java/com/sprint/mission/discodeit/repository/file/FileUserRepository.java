package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

public class FileUserRepository extends AbstractFileRepository<User> implements UserRepository {

    public FileUserRepository() {
        super("users");
    }

    @Override
    public User findByEmail(String email) {
        return dataList.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findByUserName(String userName) {
        return dataList.stream()
                .filter(user -> user.getUserName().equals(userName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findByNickName(String nickname) {
        return dataList.stream()
                .filter(user -> user.getNickname().equals(nickname))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateByEmail(String email, String userName, String nickname, String password, String phoneNumber) {
        User user = findByEmail(email);
        if (user == null) return false;

        user.updateUser(email, userName, nickname, password, phoneNumber);
        writeToFile();
        return true;
    }

    @Override
    public boolean updateByUserName(String userName, String email, String nickname, String password, String phoneNumber) {
        User user = findByUserName(userName);
        if (user == null) return false;

        user.updateUser(email, userName, nickname, password, phoneNumber);
        writeToFile();
        return true;
    }

    @Override
    public boolean deleteByEmail(String email) {
        boolean isDeleted = dataList.removeIf(user -> user.getEmail().equals(email));
        if (isDeleted) writeToFile();
        return isDeleted;
    }

    @Override
    public boolean deleteByUserName(String userName) {
        boolean isDeleted = dataList.removeIf(user -> user.getUserName().equals(userName));
        if (isDeleted) writeToFile();
        return isDeleted;
    }
}

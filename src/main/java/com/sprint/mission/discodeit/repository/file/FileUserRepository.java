package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

public class FileUserRepository extends AbstractFileRepository<User> implements UserRepository {

    public FileUserRepository() {
        super("users");
    }
}
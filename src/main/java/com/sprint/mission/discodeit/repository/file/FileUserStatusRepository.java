package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserStatusRepository extends AbstractFileRepository<UserStatus> implements UserStatusRepository {

    public FileUserStatusRepository() {
        super("userStatus");
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID id) {
        return data.values().stream()
            .filter(us -> us.getUserId().equals(id))
            .findFirst();
    }
}

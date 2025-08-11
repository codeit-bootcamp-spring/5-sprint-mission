package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileUserStatusRepository extends FileStore<UserStatus> implements UserStatusRepository {

    private final Map<UUID, UserStatus> data = new HashMap<>();

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String rootDir) {
        super(rootDir + "userStatus.ser");
        Map<UUID, UserStatus> loaded = loadFromFile();
        if (loaded != null) {
            data.putAll(loaded);
        }
    }

    // 상태 저장(갱신)
    @Override
    public void save(UserStatus userStatus){
        data.put(userStatus.getId(), userStatus);
        saveToFile(data);
    }

    // 사용자 상태 조회
    @Override
    public Optional<UserStatus> findByUserId(UUID userId){
        return data.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    // 전체 상태 조회
    @Override
    public List<UserStatus> findAll(){
        return List.copyOf(data.values());
    }


}

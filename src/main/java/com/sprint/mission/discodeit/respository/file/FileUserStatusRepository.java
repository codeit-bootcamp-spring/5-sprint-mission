package com.sprint.mission.discodeit.respository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.respository.UserStatusRepository;

import java.util.*;

public class FileUserStatusRepository extends FileStore<UserStatus> implements UserStatusRepository {

    private final Map<UUID, UserStatus> data = new HashMap<>();

    public FileUserStatusRepository(String rootDir) {
        super(rootDir + "userStatus.store");
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
    public Optional<UserStatus> findById(UUID userId){
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

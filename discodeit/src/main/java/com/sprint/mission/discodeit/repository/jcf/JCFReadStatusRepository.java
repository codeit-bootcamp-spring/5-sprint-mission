package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.Locked;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("jcfReadStatusRepository")
@Profile("jcf")
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID,ReadStatus> data;

    public JCFReadStatusRepository() {
        data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        this.data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public ReadStatus findById(UUID id) {
        ReadStatus status = data.get(id);
        if(status == null){
            return new ReadStatus();
        }
        return status;
    }

    @Override
    public List<ReadStatus> findAll() {
        List<ReadStatus> status=List.copyOf(data.values());
        return status;
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }
}

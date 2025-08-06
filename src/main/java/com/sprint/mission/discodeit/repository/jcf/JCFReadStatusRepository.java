package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {
    Map<UUID, ReadStatus> data = new HashMap<>();

    @Override
    public Optional<ReadStatus> save(ReadStatus channel) {
        if(channel == null){
            return Optional.empty();
        }
        data.put(channel.getId(), channel);
        return Optional.of(channel);
    }

    @Override
    public void deleteAll(){
        data.clear();
    }

    @Override
    public List<UUID> findUsersIdByChannelId(UUID channelId) {
        List<UUID> resultList = new ArrayList<>();

        data.forEach((k,v) -> {
            if(v.getChannelId().equals(channelId)){
                resultList.add(v.getUserId());
            }
        });

        return resultList;
    }
}

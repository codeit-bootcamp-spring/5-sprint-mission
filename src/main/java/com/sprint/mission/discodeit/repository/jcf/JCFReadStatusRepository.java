package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;


//@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {
    Map<UUID, ReadStatus> data = new HashMap<>();

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<ReadStatus> save(ReadStatus readStatus) {
        if(readStatus == null){
            return Optional.empty();
        }
        data.put(readStatus.getId(), readStatus);
        return Optional.of(readStatus);
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        if(data.containsKey(readStatusId)){
            return Optional.of(data.get(readStatusId));
        }
        return Optional.empty();
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

    @Override
    public List<UUID> findChannelsIdByUserId(UUID userId) {
        List<UUID> resultList = new ArrayList<>();

        data.forEach((k,v) -> {
            if(v.getUserId().equals(userId)){
                resultList.add(v.getChannelId());
            }
        });

        return resultList;
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.entrySet().removeIf(
                entry
                        -> entry
                        .getValue()
                        .getChannelId()
                        .equals(channelId)
        );
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> resultList = new ArrayList<>();

        data.forEach((k,v) -> {
            if(v.getUserId().equals(userId)){
                resultList.add(v);
            }
        });

        return resultList;

    }
}

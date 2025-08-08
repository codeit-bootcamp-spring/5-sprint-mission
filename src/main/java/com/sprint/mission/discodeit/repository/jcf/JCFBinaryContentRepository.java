package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

//@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {
    Map<UUID, BinaryContent> data = new HashMap<>();

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        if(!data.containsKey(id)){
            return Optional.empty();
        }
        return Optional.of(data.get(id));
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }


    @Override
    public Optional<BinaryContent> save(BinaryContent content) {
        if(content == null){
            return Optional.empty();
        }

        data.put(content.getId(), content);
        return Optional.of(content);
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data.values());
    }
}

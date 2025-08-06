package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("jcfBinaryContentRepository")
@Profile("jcf")
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> data;
    public JCFBinaryContentRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent, User user) {
        binaryContent.setUserId(UUID.randomUUID());
        data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent, Message message) {
        binaryContent.setMessageId(UUID.randomUUID());
        data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> binaryContents = new ArrayList<>();
        for (BinaryContent content : data.values()) {
            for(UUID id : ids) {
                if (id.equals(content.getUserId()) ||  id.equals(content.getMessageId())) {
                    binaryContents.add(content);
                }
            }

        }
        return binaryContents;
    }

    @Override
    public List<BinaryContent> getAllData() {
        return List.of(data.values().toArray(new BinaryContent[0]));
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }
}

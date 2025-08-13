package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("binaryContentService")
@RequiredArgsConstructor
public class BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BinaryContent create(BinaryContentRequest request){
        return binaryContentRepository.save(new BinaryContent(request.fileName(), request.contentType(), request.size(), request.bytes()));
    }

    public BinaryContent find(UUID id){
        return binaryContentRepository.findById(id).orElse(null);
    }

    public List<BinaryContent> findAllByIdIn(UUID id){
        List<BinaryContent> list = new ArrayList<>();
        for(BinaryContent content: binaryContentRepository.getAllData()){
            if(content.getMessageId().equals(id) || content.getUserId().equals(id)){
                list.add(content);
            }
        }
        if(list.isEmpty()){
            return new ArrayList<>();
        }
        return list;

    }

    public void delete(UUID id){
        binaryContentRepository.deleteById(id);
    }

}

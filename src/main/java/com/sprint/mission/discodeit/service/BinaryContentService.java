package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;

public interface BinaryContentService {

    BinaryContent addBinaryContent(byte[] binaryContent);
    List<BinaryContent> getAllBinaryContent();
}

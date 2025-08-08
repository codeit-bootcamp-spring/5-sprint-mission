package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent getBinaryContentById(UUID contentId);
    BinaryContent addBinaryContent(byte[] binaryContent);
    List<BinaryContent> getAllBinaryContent();
    void deleteBinaryContent(UUID contentId);
}

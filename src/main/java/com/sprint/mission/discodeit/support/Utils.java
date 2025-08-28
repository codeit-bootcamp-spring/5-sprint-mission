package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public final class Utils {

    public static BinaryContent toBinaryContentFromMultipartFile(MultipartFile multipartFile) {
        try {
            return new BinaryContent(
                multipartFile.getOriginalFilename(),
                multipartFile.getSize(),
                multipartFile.getContentType(),
                multipartFile.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 실패: " + multipartFile.getOriginalFilename(), e);
        }
    }

}

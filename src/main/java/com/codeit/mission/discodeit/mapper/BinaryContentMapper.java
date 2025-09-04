package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.data.BinaryContentDto;
import com.codeit.mission.discodeit.entity.BinaryContent;
import com.codeit.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinaryContentMapper {

    private final BinaryContentStorage binaryContentStorage;

    public BinaryContentDto toDto(BinaryContent binaryContent) {
        try {
            InputStream inputStream = binaryContentStorage.get(binaryContent.getId());
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();

            return new BinaryContentDto(binaryContent.getId(), binaryContent.getFileName(),
                binaryContent.getSize(), binaryContent.getContentType(), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

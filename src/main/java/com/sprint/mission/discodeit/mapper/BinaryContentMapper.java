package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BinaryContentMapper {

    public static BinaryContentResponse toBinaryContentResponse(BinaryContent bc) {
        Objects.requireNonNull(bc, "binaryContent must not be null");
        return new BinaryContentResponse(
                bc.getId(),
                bc.getFilename(),
                bc.getContentType(),
                bc.getSize(),
                bc.getBytes()
        );
    }
}

package com.sprint.mission.discodeit.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BinaryContent extends BaseEntity {
    private final byte[] bytes;
}

package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class BinaryContent extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final byte[] content;

    public BinaryContent(byte[] content) {
        super();
        this.content = content;
    }
}

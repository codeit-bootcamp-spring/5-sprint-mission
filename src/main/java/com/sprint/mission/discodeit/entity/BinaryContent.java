package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
public class BinaryContent extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final BinaryContentType contentType;
    private final byte[] content;

    public BinaryContent(byte[] content, BinaryContentType contentType) {
        super();
        this.content = content;
        this.contentType = contentType;
    }



}

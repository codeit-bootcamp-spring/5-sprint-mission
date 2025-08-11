package com.sprint.mission.discodeit.entity;

import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class BinaryContent extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String fileName;
    private Long size;
    private String contentType;
    //텍스트가 아닌 모든 종류의 데이터 원본
    private byte[] bytes;

    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
        super();
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.bytes = bytes;
    }

}

package com.sprint.mission.discodeit.entity;

public enum BinaryContentType {
    PNG("image/png"), JPG("image/jpg"), JPEG("image/jpeg"), GIF("image/git"), WEBP("image/webp"), BMP("image/bmp"), SVG("image/svg"), TIFF("image/tiff");

    public final String type;

    BinaryContentType(String type) {
        this.type = type;
    }
}

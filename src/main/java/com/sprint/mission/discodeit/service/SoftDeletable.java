package com.sprint.mission.discodeit.service;

public interface SoftDeletable {

    boolean isDeleted();    // 소프트 삭제 여부
    void delete();          // 소프트 삭제 처리
}

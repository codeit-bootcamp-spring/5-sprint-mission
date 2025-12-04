package com.sprint.mission.discodeit.infra.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class PendingBinaryContentStore {

    private final ConcurrentMap<UUID, byte[]> pendingUploads = new ConcurrentHashMap<>();

    public void put(UUID binaryContentId, byte[] bytes) {
        pendingUploads.put(binaryContentId, bytes);
        log.debug("바이너리 콘텐츠 대기열 추가: binaryContentId={}, size={}", binaryContentId, bytes.length);
    }

    public byte[] remove(UUID binaryContentId) {
        byte[] bytes = pendingUploads.remove(binaryContentId);
        if (bytes != null) {
            log.debug("바이너리 콘텐츠 대기열 제거: binaryContentId={}", binaryContentId);
        } else {
            log.warn("바이너리 콘텐츠 대기열에서 찾을 수 없음: binaryContentId={}", binaryContentId);
        }
        return bytes;
    }
}

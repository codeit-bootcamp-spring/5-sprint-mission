package com.sprint.mission.discodeit.repository.impl;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemorySseEmitterRepository implements SseEmitterRepository {

    private final Map<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(UUID userId, SseEmitter emitter) {
        emitters.compute(userId, (key, emitterList) -> {
            if (emitterList == null) {
                List<SseEmitter> newList = Collections.synchronizedList(new ArrayList<>());
                newList.add(emitter);
                log.info("[SseEmitterRepository] 새 사용자 Emitter 생성 - userId: {}", userId);
                return newList;
            } else {
                emitterList.add(emitter);
                log.info("[SseEmitterRepository] Emitter 추가 - userId: {}, 총 연결 수: {}",
                        userId, emitterList.size());
                return emitterList;
            }
        });

        return emitter;
    }

    @Override
    public Optional<List<SseEmitter>> findById(UUID userId) {
        return Optional.ofNullable(emitters.get(userId));
    }

    @Override
    public void delete(UUID userId, SseEmitter emitter) {
        emitters.computeIfPresent(userId, (key, emitterList) -> {
            emitterList.remove(emitter);

            if (emitterList.isEmpty()) {
                log.info("[SseEmitterRepository] 마지막 Emitter 제거 - userId: {}", userId);
                return null;
            } else {
                log.info("[SseEmitterRepository] Emitter 제거 - userId: {}, 남은 연결 수: {}",
                        userId, emitterList.size());
                return emitterList;
            }
        });
    }

    @Override
    public Map<UUID, List<SseEmitter>> findAll() {
        return emitters.entrySet().stream()
                .map(entry -> {
                    List<SseEmitter> original = entry.getValue();
                    synchronized (original) {
                        return Map.entry(entry.getKey(), new ArrayList<>(original));
                    }
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    @Override
    public Map<UUID, List<SseEmitter>> findAllByReceiverIdsIn(Collection<UUID> receiverIds) {
        return receiverIds.stream()
                .map(receiverId -> {
                    List<SseEmitter> original = emitters.get(receiverId);
                    if (original == null) {
                        return null;
                    }
                    synchronized (original) {
                        return Map.entry(receiverId, new ArrayList<>(original));
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}

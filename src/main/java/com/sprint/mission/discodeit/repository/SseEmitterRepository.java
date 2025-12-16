package com.sprint.mission.discodeit.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

public interface SseEmitterRepository {

    SseEmitter save(UUID userId, SseEmitter sseEmitter);

    Optional<List<SseEmitter>> findById(UUID userId);

    void delete(UUID userId, SseEmitter emitter);

    Map<UUID, List<SseEmitter>> findAll();

    Map<UUID, List<SseEmitter>> findAllByReceiverIdsIn(Collection<UUID> receiverIds);

}

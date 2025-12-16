package com.sprint.mission.discodeit.sse;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.DataWithMediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Getter
public class SseMessage {

  private final UUID eventId;
  private final String eventName;
  private final Object data;
  private final Set<UUID> receiverIds;
  private final boolean broadcast;

  private SseMessage(Set<UUID> receiverIds, String eventName, Object data, boolean broadcast) {
    this.eventId = UUID.randomUUID();
    this.eventName = eventName;
    this.data = data;
    this.receiverIds = receiverIds;
    this.broadcast = broadcast;
  }

  public static SseMessage create(Collection<UUID> receiverIds, String eventName, Object data) {
    return new SseMessage(Set.copyOf(receiverIds), eventName, data, false);
  }

  public static SseMessage createBroadcast(String eventName, Object data) {
    return new SseMessage(Set.of(), eventName, data, true);
  }

  public boolean isReceivable(UUID receiverId) {
    return broadcast || receiverIds.contains(receiverId);
  }

  public Set<DataWithMediaType> toEvent() {
    SseEmitter.SseEventBuilder builder = SseEmitter.event()
        .id(eventId.toString())
        .name(eventName)
        .data(data);

    return Set.of(new DataWithMediaType(builder, MediaType.APPLICATION_JSON));
  }
}

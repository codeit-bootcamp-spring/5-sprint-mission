package com.sprint.mission.discodeit.event.handler;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentTopicListener {
	private final BinaryContentStorage binaryContentStorage;
	private final ObjectMapper objectMapper;

	@KafkaListener(topics = "discodeit.BinaryContentCreatedEvent")
	public void onBinaryContentCreatedEvent(String kafkaEvent) throws JsonProcessingException {
		BinaryContentCreatedEvent event = objectMapper.readValue(kafkaEvent, BinaryContentCreatedEvent.class);
		log.info("BinaryContentCreatedEvent: {}", event.binaryContentId());
		binaryContentStorage.put(event.binaryContentId(), event.file());
	}

}

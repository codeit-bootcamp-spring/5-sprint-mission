package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.entity.Channels;

public interface ChannelRepository {
	public Channels save(Channels channels);

	public Optional<Channels> find(UUID id);

	public List<Channels> findAll();

	public void delete(UUID id);

	boolean existsById(UUID id);

	void deleteAll();

	Long count();
}

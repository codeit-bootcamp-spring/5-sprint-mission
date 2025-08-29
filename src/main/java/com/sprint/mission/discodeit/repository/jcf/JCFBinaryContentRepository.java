package com.sprint.mission.discodeit.repository.jcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

@Repository
@ConditionalOnProperty(
  prefix = "discodeit.repository",
  name = "type",
  havingValue = "jcf",
  matchIfMissing = true // 값이 없으면 JCF로 등록
)
public class JCFBinaryContentRepository implements BinaryContentRepository {

	public final Map<UUID, BinaryContent> data;

	public JCFBinaryContentRepository() {
		this.data = new HashMap<>();
	}

	@Override
	public BinaryContent save(BinaryContent binaryContent) {
		data.put(binaryContent.getId(), binaryContent);
		return binaryContent;
	}

	@Override
	public List<BinaryContent> saveAll(List<BinaryContent> binaryContents) {
		binaryContents.forEach(binaryContent -> data.put(binaryContent.getId(), binaryContent));
		return binaryContents;
	}

	@Override
	public Optional<BinaryContent> find(UUID id) {
		return Optional.ofNullable(data.get(id));
	}

	@Override
	public List<BinaryContent> findAll() {
		return data.values().stream().toList();
	}

	@Override
	public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
		return data.values().stream()
		  .filter(binaryContent -> ids.contains(binaryContent.getId()))
		  .toList();
	}

	@Override
	public void delete(UUID id) {
		if (!data.containsKey(id)) {
			throw new IllegalArgumentException("BinaryContent with ID " + id + " not found");
		}
		data.remove(id);
	}

	@Override
	public boolean isEmpty(UUID id) {
		return !data.containsKey(id) || data.get(id) == null;
	}

	@Override
	public void deleteAll() {
		data.clear();
	}
}

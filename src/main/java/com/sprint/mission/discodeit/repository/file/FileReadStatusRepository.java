package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, ReadStatus> storage = new HashMap<>();

  @Override
  public void save(ReadStatus readStatus) {
    storage.put(readStatus.getId(), readStatus);
  }


  //특정 채널에 속한 ReadStatus만 골라서 리스트로 반환
  @Override
  public List<ReadStatus> findByChannelId(UUID channelId) {
    System.out.println("🔍 findByChannelId() 호출됨: " + channelId);
    return storage.values().stream() // 저장된 모든 ReadStatus 꺼내서 하나하나 살펴보기
        .filter(r -> r.getChannelId().equals(channelId)) //조건에 맞는 것만 고르기
        .toList(); // 골라낸 것 리스트 반환
  }

  //특정 채널에 속한 ReadStatus를 다 지움
  @Override
  public void deleteByChannelId(UUID channelId) {
    //entrySet() : Map에 저장된 모든 짝 가져옴
    //removeIf : 조건에 맞는 것만 삭제
    //e.getValue : ReadStatus 객체
    storage.entrySet().removeIf(e -> e.getValue().getChannelId().equals(channelId));
  }

  @Override
  public List<ReadStatus> findAll() {
    return List.copyOf(storage.values()); // ✅ 여기서도 직접 storage 사용
  }

  @Override
  public ReadStatus findById(UUID id) {
    return storage.get(id);
  }
}

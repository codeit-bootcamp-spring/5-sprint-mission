package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> data = new HashMap<>();

    // ✅ 저장
    @Override
    public void save(BinaryContent content) {
        data.put(content.getId(), content);
        saveToFile(); // 저장소에 반영
    }

    // ✅ ownerId로 삭제
    @Override
    public void deleteByOwnerId(UUID ownerId) {
        // 1. 해당 ownerId를 가진 BinaryContent id만 추출
        List<UUID> targetIds = data.values().stream()
                .filter(binary -> binary.getOwnerId().equals(ownerId))
                .map(BinaryContent::getId)
                .collect(Collectors.toList());

        // 2. 삭제
        for (UUID id : targetIds) {
            data.remove(id);
        }

        // 3. 저장 반영
        saveToFile();
    }

    // ✅ 전체 조회 (추가 기능)
    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data.values());
    }

    // ✅ 임시 저장 메서드 (직렬화 구현 예정)
    private void saveToFile() {
        System.out.println("[BinaryContent] 저장 완료 (임시)");
    }
}

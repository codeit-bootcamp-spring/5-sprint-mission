package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest; // 요청 DTO
import com.sprint.mission.discodeit.entity.BinaryContent;                  // 엔티티
import com.sprint.mission.discodeit.repository.BinaryContentRepository;    // 레포지토리
import com.sprint.mission.discodeit.service.BinaryContentService;          // 서비스 인터페이스
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;                                     // 생성자 주입
import org.springframework.stereotype.Service;                             // 서비스 빈
import org.springframework.transaction.annotation.Transactional;          // 트랜잭션

import java.util.List;                                                     // List
import java.util.NoSuchElementException;                                   // 예외
import java.util.UUID;                                                     // UUID

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage storage;

    @Override
    @Transactional
    public BinaryContent create(BinaryContentCreateRequest request) {
        String fileName = request.fileName();
        String contentType = request.contentType();
        byte[] bytes = request.bytes();

        // 1) 메타 저장(아이디 확보) — 편의 생성자 사용
        BinaryContent meta = binaryContentRepository.save(
                new BinaryContent(fileName, (long) bytes.length, contentType)
        );

        // 2) 스토리지 저장 (키 = meta.getId())
        try {
            storage.put(meta.getId(), bytes);
            return meta;
        } catch (RuntimeException e) {
            // 보상 처리(스토리지 실패 시 메타 롤백)
            binaryContentRepository.deleteById(meta.getId());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContent find(UUID binaryContentId) {
        // PK로 조회, 없으면 예외
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() ->
                        new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        // 일괄 조회 (레포지토리 자체가 List 반환하므로 stream 변환 불필요)
        return binaryContentRepository.findAllByIdIn(binaryContentIds);
    }

    @Override
    @Transactional
    public void delete(UUID binaryContentId) {
        // 존재하지 않으면 예외 발생
        BinaryContent content = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() ->
                        new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));

        // 존재하면 삭제
        binaryContentRepository.delete(content);
    }
}

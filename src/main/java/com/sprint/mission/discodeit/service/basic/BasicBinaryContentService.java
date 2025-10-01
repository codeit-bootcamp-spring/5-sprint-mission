package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor // Lombok: final 필드에 대해 생성자를 자동 생성하여 DI를 간결하게 함
@Service // 스프링 서비스 컴포넌트로 등록
public class BasicBinaryContentService implements BinaryContentService { // 이 클래스는 BinaryContentService 구현체

    private final BinaryContentRepository binaryContentRepository; // 바이너리 메타데이터를 DB에 저장/조회하는 리포지토리 의존성
    private final BinaryContentMapper binaryContentMapper; // 엔티티 ↔ DTO 변환을 담당하는 매퍼 의존성
    private final BinaryContentStorage binaryContentStorage; // 실제 파일 바이트를 저장/조회하는 스토리지 의존성

    @Transactional // 트랜잭션 경계: 메타데이터 저장과 스토리지 저장을 하나의 작업으로 묶음
    @Override // 인터페이스 메서드 구현
    public BinaryContentDto create(BinaryContentCreateRequest request) { // 새 바이너리 컨텐츠를 생성하는 메서드
        String fileName = request.fileName(); // 요청에서 파일명 추출
        byte[] bytes = request.bytes(); // 요청에서 파일 바이트 배열 추출
        String contentType = request.contentType(); // 요청에서 MIME 타입 추출
        BinaryContent binaryContent = new BinaryContent( // 메타데이터 엔티티 인스턴스 생성
                fileName, // 파일명 설정
                (long) bytes.length, // 파일 크기를 바이트 길이로 설정 (long 캐스팅)
                contentType // 콘텐츠 타입 설정
        );
        binaryContentRepository.save(binaryContent); // 메타데이터를 DB에 저장 (영속화)
        binaryContentStorage.put(binaryContent.getId(), bytes); // 스토리지에 실제 바이트 저장 (키는 엔티티 ID)

        return binaryContentMapper.toDto(binaryContent); // 저장된 엔티티를 DTO로 변환하여 반환
    }

    @Override // 인터페이스 메서드 구현
    public BinaryContentDto find(UUID binaryContentId) { // ID로 바이너리 컨텐츠 조회
        return binaryContentRepository.findById(binaryContentId) // DB에서 메타데이터 조회 시도
                .map(binaryContentMapper::toDto) // 존재하면 DTO로 변환
                .orElseThrow(() -> new NoSuchElementException( // 없으면 예외 발생
                        "BinaryContent with id " + binaryContentId + " not found")); // 예외 메시지 구성
    }

    @Override // 인터페이스 메서드 구현
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) { // ID 목록으로 다건 조회
        return binaryContentRepository.findAllById(binaryContentIds).stream() // DB에서 일괄 조회 후 스트림 변환
                .map(binaryContentMapper::toDto) // 각 엔티티를 DTO로 매핑
                .toList(); // 리스트로 수집하여 반환
    }

    @Transactional // 삭제 작업을 트랜잭션으로 수행
    @Override // 인터페이스 메서드 구현
    public void delete(UUID binaryContentId) { // ID로 바이너리 컨텐츠 삭제
        if (!binaryContentRepository.existsById(binaryContentId)) { // 존재 여부 선검증
            throw new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"); // 없으면 예외
        }
        binaryContentRepository.deleteById(binaryContentId); // 메타데이터 삭제 수행 (스토리지 정리 로직은 별도일 수 있음)
    }
}


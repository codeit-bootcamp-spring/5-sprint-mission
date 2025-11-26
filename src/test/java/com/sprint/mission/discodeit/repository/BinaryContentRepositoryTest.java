package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@org.springframework.test.context.ActiveProfiles("test")
class BinaryContentRepositoryTest {

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    private BinaryContent file1;
    private BinaryContent file2;

    @BeforeEach
    void setUp() {
        // given - 파일 생성
        file1 = new BinaryContent("image1.png", 1024L, "image/png");
        file2 = new BinaryContent("document.pdf", 2048L, "application/pdf");
        BinaryContent file3 = new BinaryContent("video.mp4", 4096L, "video/mp4");

        binaryContentRepository.saveAll(List.of(file1, file2, file3));
    }

    @Test
    @DisplayName("findAllByIdIn - ID 컬렉션으로 파일들 조회 성공")
    void findAllByIdIn_Success() {
        // given
        Set<UUID> ids = Set.of(file1.getId(), file2.getId());

        // when
        List<BinaryContent> files = binaryContentRepository.findAllByIdIn(ids);

        // then
        assertThat(files).hasSize(2);
        assertThat(files).extracting(BinaryContent::getFileName)
            .containsExactlyInAnyOrder("image1.png", "document.pdf");
    }

    @Test
    @DisplayName("findAllByIdIn - 존재하지 않는 ID로 조회 시 빈 리스트 반환")
    void findAllByIdIn_NotFound() {
        // given
        Set<UUID> nonExistentIds = Set.of(UUID.randomUUID(), UUID.randomUUID());

        // when
        List<BinaryContent> files = binaryContentRepository.findAllByIdIn(nonExistentIds);

        // then
        assertThat(files).isEmpty();
    }

    @Test
    @DisplayName("findAllByIdIn - 빈 컬렉션으로 조회 시 빈 리스트 반환")
    void findAllByIdIn_EmptyCollection() {
        // given
        Set<UUID> emptyIds = Set.of();

        // when
        List<BinaryContent> files = binaryContentRepository.findAllByIdIn(emptyIds);

        // then
        assertThat(files).isEmpty();
    }

    @Test
    @DisplayName("findAllByIdIn - 일부만 존재하는 ID로 조회 시 존재하는 것만 반환")
    void findAllByIdIn_PartialMatch() {
        // given
        Set<UUID> mixedIds = Set.of(file1.getId(), UUID.randomUUID());

        // when
        List<BinaryContent> files = binaryContentRepository.findAllByIdIn(mixedIds);

        // then
        assertThat(files).hasSize(1);
        assertThat(files.get(0).getFileName()).isEqualTo("image1.png");
    }

    @Test
    @DisplayName("findById - ID로 파일 조회 성공")
    void findById_Success() {
        // when
        Optional<BinaryContent> foundFile = binaryContentRepository.findById(file1.getId());

        // then
        assertThat(foundFile).isPresent();
        assertThat(foundFile.get().getFileName()).isEqualTo("image1.png");
        assertThat(foundFile.get().getSize()).isEqualTo(1024L);
        assertThat(foundFile.get().getContentType()).isEqualTo("image/png");
    }

    @Test
    @DisplayName("findById - 존재하지 않는 ID로 조회 시 Optional.empty 반환")
    void findById_NotFound() {
        // when
        Optional<BinaryContent> foundFile = binaryContentRepository.findById(UUID.randomUUID());

        // then
        assertThat(foundFile).isEmpty();
    }

    @Test
    @DisplayName("save - 파일 생성 시 JPA Audit 필드가 자동 설정됨")
    void save_AuditFieldsAutoSet() {
        // given
        BinaryContent newFile = new BinaryContent("newfile.txt", 512L, "text/plain");

        // when
        BinaryContent savedFile = binaryContentRepository.save(newFile);

        // then
        assertThat(savedFile.getId()).isNotNull();
        assertThat(savedFile.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("delete - 파일 삭제 성공")
    void delete_Success() {
        // given
        UUID fileId = file1.getId();

        // when
        binaryContentRepository.delete(file1);

        // then
        Optional<BinaryContent> deletedFile = binaryContentRepository.findById(fileId);
        assertThat(deletedFile).isEmpty();
    }

    @Test
    @DisplayName("findAll - 모든 파일 조회 성공")
    void findAll_Success() {
        // when
        List<BinaryContent> files = binaryContentRepository.findAll();

        // then
        assertThat(files).hasSize(3);
        assertThat(files).extracting(BinaryContent::getFileName)
            .containsExactlyInAnyOrder("image1.png", "document.pdf", "video.mp4");
    }
}

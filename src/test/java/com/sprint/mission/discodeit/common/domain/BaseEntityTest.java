package com.sprint.mission.discodeit.common.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BaseEntityTest.Config.class)
@DisplayName("BaseEntity, BaseUpdatableEntity 슬라이스 테스트")
@ActiveProfiles("test")
class BaseEntityTest {

    @Autowired
    private TestBaseEntityRepository baseRepository;

    @Autowired
    private TestUpdatableEntityRepository updatableRepository;

    @Autowired
    private EntityManager entityManager;

    @TestConfiguration
    @EnableJpaAuditing
    @EnableJpaRepositories(basePackageClasses = BaseEntityTest.class)
    @EntityScan(basePackageClasses = BaseEntityTest.class)
    static class Config {
    }

    @Nested
    @DisplayName("BaseEntity")
    class BaseEntityTests {

        @Test
        @DisplayName("저장 시 ID 자동 생성")
        void save_generatesId() {
            // given
            TestBaseEntity entity = new TestBaseEntity();

            // when
            TestBaseEntity saved = baseRepository.save(entity);

            // then
            assertThat(saved.getId()).isNotNull();
        }

        @Test
        @DisplayName("저장 시 createdAt 자동 주입")
        void save_setsCreatedAt() {
            // given
            TestBaseEntity entity = new TestBaseEntity();

            // when
            TestBaseEntity saved = baseRepository.save(entity);

            // then
            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
        }

        @Test
        @DisplayName("동일 ID를 가진 엔티티는 동등함")
        void equals_withSameId_returnsTrue() {
            // given
            TestBaseEntity entity = baseRepository.saveAndFlush(new TestBaseEntity());
            UUID savedId = entity.getId();
            entityManager.clear();

            // when
            TestBaseEntity found = baseRepository.findById(savedId).orElseThrow();

            // then
            assertThat(found).isEqualTo(entity);
        }

        @Test
        @DisplayName("다른 ID를 가진 엔티티는 동등하지 않음")
        void equals_withDifferentId_returnsFalse() {
            // given
            TestBaseEntity entity1 = baseRepository.save(new TestBaseEntity());
            TestBaseEntity entity2 = baseRepository.save(new TestBaseEntity());

            // then
            assertThat(entity1).isNotEqualTo(entity2);
        }

        @Test
        @DisplayName("동일 ID를 가진 엔티티는 동일한 hashCode")
        void hashCode_withSameId_returnsSameValue() {
            // given
            TestBaseEntity entity = baseRepository.saveAndFlush(new TestBaseEntity());
            int originalHashCode = entity.hashCode();
            UUID savedId = entity.getId();
            entityManager.clear();

            // when
            TestBaseEntity found = baseRepository.findById(savedId).orElseThrow();

            // then
            assertThat(found.hashCode()).isEqualTo(originalHashCode);
        }
    }

    @Nested
    @DisplayName("BaseUpdatableEntity")
    class BaseUpdatableEntityTests {

        @Test
        @DisplayName("저장 시 updatedAt 자동 주입")
        void save_setsUpdatedAt() {
            // given
            TestUpdatableEntity entity = new TestUpdatableEntity();
            entity.updateData("initial");

            // when
            TestUpdatableEntity saved = updatableRepository.save(entity);

            // then
            assertThat(saved.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("수정 시 updatedAt 갱신")
        void update_updatesUpdatedAt() throws InterruptedException {
            // given
            TestUpdatableEntity entity = new TestUpdatableEntity();
            entity.updateData("initial");

            TestUpdatableEntity saved = updatableRepository.save(entity);
            Instant firstUpdatedAt = saved.getUpdatedAt();

            Thread.sleep(20);

            // when
            saved.updateData("updated");
            updatableRepository.saveAndFlush(saved);

            entityManager.clear();
            TestUpdatableEntity updated = updatableRepository.findById(saved.getId()).orElseThrow();

            // then
            assertThat(updated.getUpdatedAt()).isAfter(firstUpdatedAt);
        }

        @Test
        @DisplayName("수정 시 createdAt 불변")
        void update_createdAtRemainsSame() throws InterruptedException {
            // given
            TestUpdatableEntity entity = new TestUpdatableEntity();
            entity.updateData("initial");

            TestUpdatableEntity saved = updatableRepository.save(entity);
            Instant firstCreatedAt = saved.getCreatedAt();

            Thread.sleep(20);

            // when
            saved.updateData("updated");
            updatableRepository.saveAndFlush(saved);

            entityManager.clear();
            TestUpdatableEntity updated = updatableRepository.findById(saved.getId()).orElseThrow();

            // then
            assertThat(updated.getCreatedAt()).isEqualTo(firstCreatedAt);
        }

        @Test
        @DisplayName("BaseEntity 필드 상속 확인")
        void inheritsBaseEntityFields() {
            // given
            TestUpdatableEntity entity = new TestUpdatableEntity();
            entity.updateData("test");

            // when
            TestUpdatableEntity saved = updatableRepository.save(entity);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("저장 시 createdAt과 updatedAt은 동일하게 설정됨")
        void save_setsDates() {
            // given
            TestUpdatableEntity entity = new TestUpdatableEntity();

            // when
            TestUpdatableEntity saved = updatableRepository.save(entity);

            // then
            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getUpdatedAt()).isNotNull();

            assertThat(saved.getCreatedAt()).isEqualTo(saved.getUpdatedAt());
        }
    }
}

interface TestBaseEntityRepository extends JpaRepository<TestBaseEntity, UUID> {
}

interface TestUpdatableEntityRepository extends JpaRepository<TestUpdatableEntity, UUID> {
}

@Entity
class TestBaseEntity extends BaseEntity {
}

@Entity
class TestUpdatableEntity extends BaseUpdatableEntity {
    private String data;

    public void updateData(String data) {
        this.data = data;
    }
}

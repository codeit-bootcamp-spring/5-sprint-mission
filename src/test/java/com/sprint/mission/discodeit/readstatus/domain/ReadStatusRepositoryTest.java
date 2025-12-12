package com.sprint.mission.discodeit.readstatus.domain;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.global.config.JpaConfig;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("ReadStatusRepository 슬라이스 테스트")
@ActiveProfiles("test")
class ReadStatusRepositoryTest {

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private EntityManager entityManager;

    private User user1;
    private User user2;
    private Channel channel1;
    private Channel channel2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User("testuser1", "test1@example.com", "password1234", null));
        user2 = userRepository.save(new User("testuser2", "test2@example.com", "password1234", null));

        channel1 = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "General channel"));
        channel2 = channelRepository.save(new Channel(ChannelType.PUBLIC, "random", "Random channel"));
    }

    @Nested
    @DisplayName("findAllByUserId")
    class FindAllByUserId {

        @Test
        @DisplayName("사용자 ID로 ReadStatus 목록 조회 성공")
        void returnsReadStatusesForUser() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user1, channel2, Instant.now(), false));
            readStatusRepository.save(new ReadStatus(user2, channel1, Instant.now(), true));

            // when
            List<ReadStatus> result = readStatusRepository.findAllByUserId(user1.getId());

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(rs -> rs.getUser().getId().equals(user1.getId()));
        }

        @Test
        @DisplayName("사용자의 모든 ReadStatus 조회 성공")
        void returnsAllReadStatusesForUser() {
            // given
            ReadStatus rs1 = readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));
            ReadStatus rs2 = readStatusRepository.save(new ReadStatus(user1, channel2, Instant.now(), false));

            // 다른 유저의 데이터 (조회되면 안 됨)
            readStatusRepository.save(new ReadStatus(user2, channel1, Instant.now(), true));

            // when
            List<ReadStatus> result = readStatusRepository.findAllByUserId(user1.getId());

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(ReadStatus::getId)
                .containsExactlyInAnyOrder(rs1.getId(), rs2.getId());
        }

        @Test
        @DisplayName("해당 사용자의 ReadStatus가 없는 경우 빈 목록 반환")
        void returnsEmptyList_whenNoReadStatus() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));

            // when
            List<ReadStatus> result = readStatusRepository.findAllByUserId(user2.getId());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllWithChannelByUserId")
    class FindAllWithChannelByUserId {

        @Test
        @DisplayName("사용자 ID로 Channel과 함께 ReadStatus 목록 조회 성공 및 Eager Loading 검증")
        void returnsReadStatusesWithChannel() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user1, channel2, Instant.now(), false));

            entityManager.flush();
            entityManager.clear();

            // when
            List<ReadStatus> result = readStatusRepository.findAllWithChannelByUserId(user1.getId());

            // then
            assertThat(result).hasSize(2);

            PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
            for (ReadStatus rs : result) {
                assertThat(util.isLoaded(rs.getChannel()))
                    .as("EntityGraph를 통해 Channel이 Eager Loading 되어야 함")
                    .isTrue();
            }

            assertThat(result).extracting(rs -> rs.getChannel().getName())
                .containsExactlyInAnyOrder("general", "random");
        }

        @Test
        @DisplayName("사용자의 모든 ReadStatus 조회 성공")
        void returnsAllReadStatusesForUser() {
            // given
            ReadStatus rs1 = readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));
            ReadStatus rs2 = readStatusRepository.save(new ReadStatus(user1, channel2, Instant.now(), false));

            // 다른 유저의 데이터 (조회되면 안 됨)
            readStatusRepository.save(new ReadStatus(user2, channel1, Instant.now(), true));

            // when
            List<ReadStatus> result = readStatusRepository.findAllWithChannelByUserId(user1.getId());

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(ReadStatus::getId)
                .containsExactlyInAnyOrder(rs1.getId(), rs2.getId());
        }

        @Test
        @DisplayName("해당 사용자의 ReadStatus가 없는 경우 빈 목록 반환")
        void returnsEmptyList_whenNoReadStatus() {
            // when
            List<ReadStatus> result = readStatusRepository.findAllWithChannelByUserId(user1.getId());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllWithUserProfileByChannelIdIn")
    class FindAllWithUserProfileByChannelIdIn {

        @Test
        @DisplayName("채널 ID 목록으로 User와 Profile 포함 ReadStatus 조회 성공 및 Eager Loading 검증")
        void returnsReadStatusesWithUserProfile() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user2, channel1, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user1, channel2, Instant.now(), false));

            entityManager.flush();
            entityManager.clear();

            // when
            List<ReadStatus> result = readStatusRepository
                .findAllWithUserProfileByChannelIdIn(List.of(channel1.getId(), channel2.getId()));

            // then
            assertThat(result).hasSize(3);

            PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

            for (ReadStatus rs : result) {
                assertThat(util.isLoaded(rs.getUser()))
                    .as("User 엔티티가 Eager Loading 되어야 함")
                    .isTrue();

                assertThat(util.isLoaded(rs.getUser().getProfile()))
                    .as("User의 Profile까지 Eager Loading 되어야 함")
                    .isTrue();
            }
        }

        @Test
        @DisplayName("채널 ID 목록이 비어있는 경우 빈 목록 반환")
        void returnsEmptyList_whenChannelIdsEmpty() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));

            // when
            List<ReadStatus> result = readStatusRepository.findAllWithUserProfileByChannelIdIn(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("일치하는 채널이 없는 경우 빈 목록 반환")
        void returnsEmptyList_whenNoMatchingChannels() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));

            // when
            List<ReadStatus> result = readStatusRepository
                .findAllWithUserProfileByChannelIdIn(List.of(UUID.randomUUID()));

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findNotificationTargets")
    class FindNotificationTargets {

        @Test
        @DisplayName("알림 활성화된 ReadStatus 조회 성공 (작성자 제외) 및 Eager Loading 검증")
        void returnsEnabledReadStatuses_excludingAuthor() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user2, channel1, Instant.now(), true));

            entityManager.flush();
            entityManager.clear();

            // when
            List<ReadStatus> result = readStatusRepository
                .findNotificationTargets(channel1.getId(), user1.getId());

            // then
            assertThat(result).hasSize(1);
            ReadStatus target = result.get(0);

            assertThat(target.getUser().getId()).isEqualTo(user2.getId());

            PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
            assertThat(util.isLoaded(target.getUser()))
                .as("User 엔티티는 Fetch Join을 통해 Eager Loading 되어야 함")
                .isTrue();
        }

        @Test
        @DisplayName("알림 비활성화된 ReadStatus 제외")
        void excludesDisabledNotifications() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user2, channel1, Instant.now(), false));

            // when
            List<ReadStatus> result = readStatusRepository
                .findNotificationTargets(channel1.getId(), user1.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("작성자 본인 결과에서 제외")
        void excludesAuthor() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));

            // when
            List<ReadStatus> result = readStatusRepository
                .findNotificationTargets(channel1.getId(), user1.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("해당 채널에 ReadStatus가 없는 경우 빈 목록 반환")
        void returnsEmptyList_whenNoReadStatusInChannel() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));

            // when
            List<ReadStatus> result = readStatusRepository
                .findNotificationTargets(channel2.getId(), user1.getId());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findUserIdSetByChannelId")
    class FindUserIdSetByChannelId {

        @Test
        @DisplayName("채널 ID로 사용자 ID 집합 조회 성공")
        void returnsUserIdSet() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user2, channel1, Instant.now(), false));

            // when
            Set<UUID> result = readStatusRepository.findUserIdSetByChannelId(channel1.getId());

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder(user1.getId(), user2.getId());
        }

        @Test
        @DisplayName("해당 채널에 ReadStatus가 없는 경우 빈 집합 반환")
        void returnsEmptySet_whenNoReadStatus() {
            // when
            Set<UUID> result = readStatusRepository.findUserIdSetByChannelId(channel1.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 채널 ID로 조회 시 빈 집합 반환")
        void returnsEmptySet_whenChannelDoesNotExist() {
            // when
            Set<UUID> result = readStatusRepository.findUserIdSetByChannelId(UUID.randomUUID());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteAllByChannelId")
    class DeleteAllByChannelId {

        @Test
        @DisplayName("채널 ID로 ReadStatus 삭제 성공 및 영속성 컨텍스트 초기화 검증")
        void deletesReadStatusesByChannelId() {
            // given
            ReadStatus target1 = readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user2, channel1, Instant.now(), false));
            readStatusRepository.save(new ReadStatus(user1, channel2, Instant.now(), true));

            // when
            int deletedCount = readStatusRepository.deleteAllByChannelId(channel1.getId());

            // then
            assertThat(deletedCount).isEqualTo(2);
            assertThat(readStatusRepository.findUserIdSetByChannelId(channel1.getId())).isEmpty();
            assertThat(readStatusRepository.findUserIdSetByChannelId(channel2.getId())).hasSize(1);

            Optional<ReadStatus> deletedStatus = readStatusRepository.findById(target1.getId());
            assertThat(deletedStatus).isEmpty();
        }

        @Test
        @DisplayName("해당 채널에 ReadStatus가 없는 경우 0 반환")
        void returnsZero_whenNoReadStatus() {
            // when
            int deletedCount = readStatusRepository.deleteAllByChannelId(channel1.getId());

            // then
            assertThat(deletedCount).isZero();
        }

        @Test
        @DisplayName("존재하지 않는 채널 ID로 삭제 시 0 반환")
        void returnsZero_whenChannelDoesNotExist() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));

            // when
            int deletedCount = readStatusRepository.deleteAllByChannelId(UUID.randomUUID());

            // then
            assertThat(deletedCount).isZero();
        }
    }

    @Nested
    @DisplayName("deleteAllByUserId")
    class DeleteAllByUserId {

        @Test
        @DisplayName("사용자 ID로 ReadStatus 삭제 성공")
        void deletesReadStatusesByUserId() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user1, channel2, Instant.now(), false));
            readStatusRepository.save(new ReadStatus(user2, channel1, Instant.now(), true));

            // when
            int deletedCount = readStatusRepository.deleteAllByUserId(user1.getId());

            // then
            assertThat(deletedCount).isEqualTo(2);
            assertThat(readStatusRepository.findAllByUserId(user1.getId())).isEmpty();
            assertThat(readStatusRepository.findAllByUserId(user2.getId())).hasSize(1);
        }

        @Test
        @DisplayName("해당 사용자의 ReadStatus가 없는 경우 0 반환")
        void returnsZero_whenNoReadStatus() {
            // when
            int deletedCount = readStatusRepository.deleteAllByUserId(user1.getId());

            // then
            assertThat(deletedCount).isZero();
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 삭제 시 0 반환")
        void returnsZero_whenUserDoesNotExist() {
            // given
            readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));

            // when
            int deletedCount = readStatusRepository.deleteAllByUserId(UUID.randomUUID());

            // then
            assertThat(deletedCount).isZero();
        }
    }
}

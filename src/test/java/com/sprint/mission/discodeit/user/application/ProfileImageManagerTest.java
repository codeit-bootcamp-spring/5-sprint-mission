package com.sprint.mission.discodeit.user.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.domain.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.user.domain.exception.UserProfileUploadException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileImageManager 단위 테스트")
class ProfileImageManagerTest {

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ProfileImageManager profileImageManager;

    @Captor
    private ArgumentCaptor<BinaryContentCreatedEvent> eventCaptor;

    private static final String FILE_NAME = "profile.png";
    private static final long FILE_SIZE = 1024L;
    private static final String CONTENT_TYPE = "image/png";
    private static final byte[] FILE_BYTES = "fake-image-content".getBytes();

    @Nested
    @DisplayName("save 메서드")
    class SaveTest {

        @Test
        @DisplayName("유효한 파일 저장 시 BinaryContent 반환 및 이벤트 발행")
        void save_withValidFile_savesBinaryContentAndPublishesEvent() throws IOException {
            // given
            MultipartFile file = mock(MultipartFile.class);
            given(file.isEmpty()).willReturn(false);
            given(file.getBytes()).willReturn(FILE_BYTES);
            given(file.getOriginalFilename()).willReturn(FILE_NAME);
            given(file.getSize()).willReturn(FILE_SIZE);
            given(file.getContentType()).willReturn(CONTENT_TYPE);

            UUID savedId = UUID.randomUUID();
            BinaryContent savedContent = mock(BinaryContent.class);
            given(savedContent.getId()).willReturn(savedId);
            given(savedContent.getFileName()).willReturn(FILE_NAME);
            given(savedContent.getSize()).willReturn(FILE_SIZE);
            given(savedContent.getContentType()).willReturn(CONTENT_TYPE);
            given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedContent);

            // when
            BinaryContent result = profileImageManager.save(file);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getFileName()).isEqualTo(FILE_NAME);
            assertThat(result.getSize()).isEqualTo(FILE_SIZE);
            assertThat(result.getContentType()).isEqualTo(CONTENT_TYPE);

            then(binaryContentRepository).should().save(any(BinaryContent.class));
            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            BinaryContentCreatedEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.binaryContentId()).isEqualTo(savedId);
            assertThat(capturedEvent.bytes()).isEqualTo(FILE_BYTES);
        }

        @Test
        @DisplayName("null 또는 빈 파일인 경우 null 반환")
        void save_withNullOrEmptyFile_returnsNull() {
            // null 파일
            assertThat(profileImageManager.save(null)).isNull();

            // 빈 파일
            MockMultipartFile emptyFile = new MockMultipartFile(
                "profile", FILE_NAME, CONTENT_TYPE, new byte[0]);
            assertThat(profileImageManager.save(emptyFile)).isNull();

            then(binaryContentRepository).should(never()).save(any());
            then(eventPublisher).should(never()).publishEvent(any());
        }

        @Test
        @DisplayName("파일 읽기 중 IOException 발생 시 UserProfileUploadException 발생")
        void save_whenIOExceptionOccurs_throwsUserProfileUploadException() throws IOException {
            // given
            MultipartFile file = mock(MultipartFile.class);
            given(file.isEmpty()).willReturn(false);
            given(file.getBytes()).willThrow(new IOException("File read error"));

            // when & then
            assertThatThrownBy(() -> profileImageManager.save(file))
                .isInstanceOf(UserProfileUploadException.class)
                .hasCauseInstanceOf(IOException.class);

            then(binaryContentRepository).should(never()).save(any());
            then(eventPublisher).should(never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class DeleteTest {

        @Test
        @DisplayName("프로필이 존재하면 삭제 성공")
        void delete_withExistingProfile_deletesSuccessfully() {
            // given
            BinaryContent profile = new BinaryContent(FILE_NAME, FILE_SIZE, CONTENT_TYPE);

            // when
            profileImageManager.delete(profile);

            // then
            then(binaryContentRepository).should().delete(profile);
        }

        @Test
        @DisplayName("null 프로필인 경우 삭제하지 않음")
        void delete_withNullProfile_doesNothing() {
            // when
            profileImageManager.delete(null);

            // then
            then(binaryContentRepository).should(never()).delete(any());
        }
    }
}

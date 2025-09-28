package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.UUID;


@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local") // 설정값이 local일 때만 빈 등록 (yml)
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage { // 로컬 파일 시스템 기반 저장소 구현체

    private final Path root; // 파일을 저장할 루트 디렉터리 경로

    public LocalBinaryContentStorage( // 생성자 주입
       @Value("${discodeit.storage.local.root-path}") Path root // 설정에서 루트 경로 주입
    ) {
        this.root = root; // 필드에 할당
    }

    @PostConstruct // 빈 초기화 직후 1회 실행
    public void init() { // 루트 디렉터리가 없으면 생성
        if (!Files.exists(root)) { // 루트 경로가 없으면
            try {
                Files.createDirectories(root); // 디렉터리(중간 경로 포함) 생성
            } catch (IOException e) { // 생성 중 I/O 에러
                e.printStackTrace(); // 스택트레이스 출력(로깅 대체)
                throw new RuntimeException(e); // 런타임 예외 전파
            }
        }
    }

    public UUID put(UUID binaryContentId, byte[] bytes) { // 특정 ID로 바이너리를 저장
        Path filePath = resolvePath(binaryContentId); // 저장할 실제 파일 경로 계산, // UUID → Path 변환
        if (Files.exists(filePath)) { // 같은 키 파일이 이미 존재하면
            throw new IllegalArgumentException("File with key " + binaryContentId + " already exists"); // 중복 예외
        }
        try (OutputStream outputStream = Files.newOutputStream(filePath)) { // 파일 출력 스트림 열기 (try-with-resources로 자동 close)
            outputStream.write(bytes); // 바이트 쓰기
        } catch (IOException e) { // 쓰기 중 에러
            throw new RuntimeException(e); // 런타임 예외 전파
        }
        return binaryContentId; // 저장한 파일의 키(식별자) 반환
    }

    @Override
    public InputStream get(UUID binaryContentId) { // 특정 ID의 바이너리 입력 스트림 열기
        Path filePath = resolvePath(binaryContentId); // 파일 경로 계산
        if (Files.notExists(filePath)) { // (⚠️ 논리 오류) 존재하면 예외 던짐
            throw new IllegalArgumentException("File with key " + binaryContentId + " does not exists"); // 존재하지 않는다고 예외(문구도 반대)
        }
        try {
            return Files.newInputStream(filePath); // 파일 입력 스트림 반환 (호출자가 close해야 함)
        } catch (IOException e) { // 열기 실패
            e.printStackTrace(); // 스택트레이스 출력
            throw new RuntimeException(e); // 런타임 예외 전파
        }
    }

    private Path resolvePath(UUID key) { // UUID 키를 실제 파일 경로로 매핑
        return root.resolve(key.toString()); // root/<uuid 문자열> 형태로 결합
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto metaData) { // HTTP 응답 형태로 다운로드 제공
        InputStream inputStream = get(metaData.id()); // 저장소에서 입력 스트림 가져오기
        Resource resource = new InputStreamResource(inputStream); // InputStream을 Resource로 감싸기

        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + metaData.fileName() + "\"") // (⚠️ 주의) InputStreamResource는 보통 파일명 null
                .header(HttpHeaders.CONTENT_TYPE, metaData.contentType()) // 콘텐츠 타입 설정 (ex. image/png)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metaData.size())) // 파일 크기 설정
                .body(resource); // 바디에 리소스 스트림을 실어 반환
    }
}


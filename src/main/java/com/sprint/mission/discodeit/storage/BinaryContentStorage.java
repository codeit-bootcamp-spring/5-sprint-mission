package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

public interface BinaryContentStorage {
    /**
     * 키(ID)를 기준으로 바이트 데이터를 저장합니다.
     * @param key BinaryContent의 ID
     * @param bytes 저장할 파일의 바이트 데이터
     * @return 저장된 데이터의 키 (ID)
     */
    UUID put(UUID key, byte[] bytes);

    /**
     * 키(ID)를 기준으로 저장된 데이터를 InputStream으로 반환합니다.
     * @param key BinaryContent의 ID
     * @return 파일 데이터의 InputStream
     */
    InputStream get(UUID key);

    /**
     * BinaryContent 메타데이터를 기반으로 파일을 다운로드할 수 있는 응답을 생성합니다.
     * @param binaryContentDto 파일의 메타데이터 DTO
     * @return 파일 다운로드를 위한 ResponseEntity<Resource>
     */
    ResponseEntity<Resource> download(BinaryContentDto binaryContentDto);
}

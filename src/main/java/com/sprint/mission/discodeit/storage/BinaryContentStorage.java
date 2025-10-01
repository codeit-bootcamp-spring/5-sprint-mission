package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

public interface BinaryContentStorage {
    /** BinaryContent의 id를 키로 사용해 저장 */
    UUID put(UUID binaryContentId, byte[] data);

    /** 저장소에서 바이너리를 읽어서 스트림으로 반환 */
    InputStream get(UUID binaryContentId);

    /** HTTP 다운로드 응답 생성 (헤더/타입/길이 등 세팅) */
    ResponseEntity<?> download(BinaryContentDto metaData);
}

package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

// 바이너리 데이터의 저장/로드 담당
public interface BinaryContentStorage {
    // UUID 키를 바탕으로 byte[] 데이터 저장
    // BinaryContent의 Id
    UUID put(UUID id, byte[] bytes);
    
    // 키 정보를 바탕으로 byte[] 데이터를 읽어 InputStream 타입으로 반환
    InputStream get(UUID id);
    
    // HTTP API로 다운로드 기능 제공
    // BinaryContentDto 정보를 바탕으로 파일을 다운로드할 수 있는 응답 반환
    ResponseEntity<?> download(BinaryContentDto binaryContentDto);
}

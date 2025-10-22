package com.sprint.mission.discodeit.storage.s3;


import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

/*파일/바이트/UUID/다운로드 등의 설계도 역할 인터페이스
 */

public interface BinaryContentStorage {

  UUID put(UUID id, byte[] bytes); //업로드

  InputStream get(UUID id); // 다운로드

  ResponseEntity<?> download(BinaryContentDto dto); // URL

}

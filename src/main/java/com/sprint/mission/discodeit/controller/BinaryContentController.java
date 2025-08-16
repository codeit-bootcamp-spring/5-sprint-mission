package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.ByteArrayOutputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    /**
     * [GET] BinaryContent 단건 조회 (심화 요구사항)
     * - URL: /api/binaryContent/find?binaryContentId=...
     * - 응답: ResponseEntity<BinaryContent>
     *   (bytes는 Base64로 직렬화되어 내려갑니다)
     */
    @RequestMapping(path = "find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(@RequestParam UUID binaryContentId) {
        BinaryContent file = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(file);
    }

    /**
     * (유지) 단건 다운로드: Content-Disposition: attachment
     */
    @RequestMapping(path = "download/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadOne(@PathVariable("id") UUID id) {
        BinaryContent file = binaryContentService.find(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(file.getFileName(), StandardCharsets.UTF_8)
                .build());

        return new ResponseEntity<>(file.getBytes(), headers, HttpStatus.OK);
    }

    /**
     * (유지) 여러 건 ZIP 다운로드
     * - Body: [ "uuid1", "uuid2", ... ]
     */
    @RequestMapping(path = "download", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> downloadMany(@RequestBody List<UUID> ids) {
        List<BinaryContent> files = binaryContentService.findAllByIdIn(ids);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (BinaryContent f : files) {
                ZipEntry entry = new ZipEntry(f.getFileName());
                zos.putNextEntry(entry);
                zos.write(f.getBytes());
                zos.closeEntry();
            }
            zos.finish();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("files.zip", StandardCharsets.UTF_8)
                    .build());

            return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

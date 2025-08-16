package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/binary")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // [GET] 단건 다운로드
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

    // [POST] 여러 건 다운로드 (zip)
    @RequestMapping(path = "download", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
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

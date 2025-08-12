package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.util.FileNames;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/binary-contents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    private static final List<String> ACCEPTS = List.of(
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE
    );

    @RequestMapping(method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    MediaType.IMAGE_PNG_VALUE,
                    MediaType.IMAGE_JPEG_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BinaryContentResponse> uploadBinaryContent(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Disposition", required = false) String contentDisposition,
            @RequestBody byte[] body
    ) {
        if (body == null || body.length == 0) throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");

        String ct = normalizeContentType(contentType);
        String original = parseFilename(contentDisposition);
        String fileName = FileNames.randomWithExtension(original, ct);


        BinaryContentResponse created = binaryContentService.create(
                new BinaryContentCreateRequest(fileName, ct, body)
        );
        return ResponseEntity
                .created(URI.create("/api/binaryContents/" + created.id()))
                .body(created);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BinaryContentResponse> find(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(binaryContentService.find(id));
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BinaryContentResponse>> findAll() {
        return ResponseEntity.ok(binaryContentService.findAll());
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        binaryContentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private static String normalizeContentType(String ct) {
        String value = (ct == null || ct.isBlank()) ? MediaType.APPLICATION_OCTET_STREAM_VALUE : ct;
        return ACCEPTS.contains(value) ? value : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    private static String parseFilename(String contentDisposition) {
        if (contentDisposition == null || contentDisposition.isBlank()) return null;
        try {
            return org.springframework.http.ContentDisposition.parse(contentDisposition).getFilename();
        } catch (Exception ignore) {
            return null;
        }
    }
}
package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.response.binaryContent.Base64BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @RequestMapping(path = "/find", method = RequestMethod.GET)
    public ResponseEntity<Base64BinaryContentResponse> findBinaryContentForList(
            @RequestParam UUID binaryContentId) throws IOException {
        log.info("[BinaryContentController] 파일 조회 요청 받음: {}", binaryContentId);
        BinaryContentResponse response = binaryContentService.getById(binaryContentId);
        Base64BinaryContentResponse base64Response = Base64BinaryContentResponse.fromResponse(response);
        log.info("[BinaryContentController] 파일 조회 응답");
        return ResponseEntity.ok(base64Response);
    }

    @RequestMapping(path = "/findContents", method = RequestMethod.GET)
    public ResponseEntity<List<Base64BinaryContentResponse>> findBinaryContentsForList(
            @RequestParam List<UUID> binaryContentIds) throws IOException {
        log.info("[BinaryContentController] 파일 리스트 조회 요청 받음: {}", binaryContentIds);
        List<BinaryContentResponse> responses = binaryContentService.getAllByIdIn(binaryContentIds);

        List<Base64BinaryContentResponse> base64Responses = responses.stream()
                .map(Base64BinaryContentResponse::fromResponse)
                .toList();

        log.info("[BinaryContentController] 파일 리스트 조회 응답 수");
        return ResponseEntity.ok(base64Responses);
    }

    @RequestMapping(path = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<Base64BinaryContentResponse> findBinaryContent(
            @PathVariable UUID binaryContentId) throws IOException {
        log.info("[BinaryContentController] 파일 조회 요청 받음");
        BinaryContentResponse response = binaryContentService.getById(binaryContentId);
        Base64BinaryContentResponse base64Response = Base64BinaryContentResponse.fromResponse(response);
        log.info("[BinaryContentController] 파일 조회 응답 수");
        return ResponseEntity.ok(base64Response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Base64BinaryContentResponse>> findBinaryContents(
            @RequestParam List<UUID> binaryContentIds) throws IOException {
        log.info("[BinaryContentController] 파일 리스트 조회 요청) 받음: {}", binaryContentIds);
        List<BinaryContentResponse> responses = binaryContentService.getAllByIdIn(binaryContentIds);

        List<Base64BinaryContentResponse> base64Responses = responses.stream()
                .map(Base64BinaryContentResponse::fromResponse)
                .toList();

        log.info("[BinaryContentController] 파일 리스트 조회 응답");
        return ResponseEntity.ok(base64Responses);
    }

    @RequestMapping(path = "/{binaryContentId}/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(@PathVariable UUID binaryContentId) throws IOException {
        log.info("[BinaryContentController] 파일 다운로드 요청 받음: {}", binaryContentId);
        BinaryContentDTO dto = binaryContentService.download(binaryContentId);
        log.info("[BinaryContentController] 파일 다운로드 응답 준비");
        return binaryContentStorage.download(dto);
    }
}
package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.GuildCreateRequest;
import com.sprint.mission.discodeit.dto.response.GuildResponse;
import com.sprint.mission.discodeit.service.GuildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/guilds")
public class GuildController {

    private final GuildService guildService;

    @PostMapping
    public ResponseEntity<GuildResponse> create(@Valid @RequestBody GuildCreateRequest body) {
        GuildResponse res = guildService.create(body);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(res.id()).toUri();
        return ResponseEntity.created(location).body(res);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<GuildResponse> find(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(guildService.find(id));
    }

    @GetMapping
    public ResponseEntity<List<GuildResponse>> findAll() {
        return ResponseEntity.ok(guildService.findAll());
    }

    @GetMapping(path = "/joined")
    public ResponseEntity<List<GuildResponse>> findGuildsJoinedByUser(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(guildService.findGuildsJoinedByUser(userId));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteGuild(@PathVariable("id") UUID guildId,
                                            @RequestParam("ownerId") UUID ownerId) {
        guildService.deleteGuild(guildId, ownerId);
        return ResponseEntity.noContent().build();
    }
}

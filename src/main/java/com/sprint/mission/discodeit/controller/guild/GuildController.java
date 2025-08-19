package com.sprint.mission.discodeit.controller.guild;

import com.sprint.mission.discodeit.service.guild.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/guilds")
public class GuildController {

  private final GuildService guildService;

  // @PostMapping
  // public ResponseEntity<GuildResponse> create(@Valid @RequestBody GuildCreateRequest body) {
  //   GuildResponse res = guildService.create(body);
  //   URI location = ServletUriComponentsBuilder
  //       .fromCurrentRequest().path("/{id}")
  //       .buildAndExpand(res.id()).toUri();
  //   return ResponseEntity.created(location).body(res);
  // }
  //
  // @GetMapping(path = "/{id}")
  // public ResponseEntity<GuildResponse> find(@PathVariable("id") UUID id) {
  //   return ResponseEntity.ok(guildService.find(id));
  // }
  //
  // @GetMapping
  // public ResponseEntity<List<GuildResponse>> findAll() {
  //   return ResponseEntity.ok(guildService.findAll());
  // }
  //
  // @GetMapping(path = "/joined")
  // public ResponseEntity<List<GuildResponse>> findGuildsJoinedByUser(
  //     @RequestParam("userId") UUID userId) {
  //   return ResponseEntity.ok(guildService.findGuildsJoinedByUser(userId));
  // }
  //
  // @DeleteMapping(path = "/{id}")
  // public ResponseEntity<Void> deleteGuild(@PathVariable("id") UUID guildId,
  //     @RequestParam("ownerId") UUID ownerId) {
  //   guildService.deleteGuild(guildId, ownerId);
  //   return ResponseEntity.noContent().build();
  // }
}

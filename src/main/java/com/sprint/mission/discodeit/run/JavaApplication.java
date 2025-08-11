//     private void showFriends() {
//         if (me == null) {
//             System.out.println("로그인이 필요합니다.");
//             return;
//         }
//
//         List<User> friends = userService.getFriends(me.getId()).stream().toList();
//         if (friends.isEmpty()) {
//             System.out.println("친구 : 없음");
//             return;
//         }
//
//         System.out.println("\n친구 목록:");
//         friends.forEach(f -> System.out.printf("- 별명: %s | 사용자명: %s | 이메일: %s%n", f.getGlobalName(), f.getUsername(), f.getEmail()));
//     }
//
//     private void sendFriendRequest() {
//         printGuidePrevious();
//         while (true) {
//             String email = InputHandler.getValidEmail("친구 요청할 이메일 : ");
//             if (email == null) {
//                 return;
//             }
//
//             User receiver = userService.findByEmail(email).orElse(null);
//             if (receiver == null) {
//                 System.out.println("⚠ 해당 이메일로 등록된 사용자가 없습니다.");
//                 continue;
//             }
//
//             if (me.equals(receiver)) {
//                 System.out.println("⚠ 자기 자신에게는 친구 요청을 보낼 수 없습니다.");
//                 continue;
//             }
//
//             if (userService.findById(me.getId()).orElseThrow().getFriends().contains(receiver.getId())) {
//                 System.out.println("⚠ 이미 친구입니다.");
//                 continue;
//             }
//
//             try {
//                 friendRequestService.save(new FriendRequest(me.getId(), receiver.getId()));
//                 System.out.println("✅ 친구 요청을 보냈습니다.");
//             } catch (IllegalArgumentException | IllegalStateException e) {
//                 System.out.println(e.getMessage());
//             } catch (NoSuchElementException e) {
//                 System.out.println("⚠ 유저를 찾을 수 없습니다.");
//             } catch (Exception e) {
//                 System.out.println("알 수 없는 오류: " + e.getMessage());
//             }
//         }
//     }
//
//     private void viewReceivedFriendRequests() {
//         while (true) {
//             List<FriendRequest> friendRequests = friendRequestService.getReceivedRequests(me.getId());
//
//             if (friendRequests.isEmpty()) {
//                 System.out.println("\n받은 친구 요청이 없습니다.");
//                 return;
//             }
//
//             System.out.println("\n받은 친구 요청 목록:");
//             for (int i = 0; i < friendRequests.size(); i++) {
//                 FriendRequest fr = friendRequests.get(i);
//                 Optional<User> sender = userService.findById(fr.getSenderId());
//                 String senderInfo = sender.map(u -> String.format("%s (%s)", u.getGlobalName(), u.getEmail())).orElse("알 수 없음");
//                 System.out.println((i + 1) + ". " + senderInfo);
//             }
//
//             printGuidePrevious();
//             while (true) {
//                 String idxStr = InputHandler.getInputOrBack("선택 : ");
//                 if (idxStr == null) {
//                     return;
//                 }
//
//                 try {
//                     int idx = Integer.parseInt(idxStr);
//                     if (idx < 1 || idx > friendRequests.size()) {
//                         System.out.println("유효한 번호를 입력해주세요.");
//                         continue;
//                     }
//
//                     FriendRequest selected = friendRequests.get(idx - 1);
//
//                     Boolean accepted = InputHandler.getYesOrNo("친구 요청 수락");
//                     if (accepted == null) {
//                         return;
//                     }
//
//                     if (accepted) {
//                         friendRequestService.acceptFriendRequest(selected.getId());
//                         System.out.println("친구 요청을 수락했습니다.");
//                     } else {
//                         friendRequestService.declineFriendRequest(selected.getId());
//                         System.out.println("친구 요청을 거절했습니다.");
//                     }
//                     break;
//                 } catch (NumberFormatException e) {
//                     System.out.println("숫자를 입력해주세요.");
//                 } catch (Exception e) {
//                     System.out.println(e.getMessage());
//                 }
//             }
//         }
//     }
//
//     private void viewSentFriendRequests() {
//         while (true) {
//             List<FriendRequest> friendRequests = friendRequestService.getSentRequests(me.getId());
//
//             if (friendRequests.isEmpty()) {
//                 System.out.println("\n보낸 친구 요청이 없습니다.");
//                 return;
//             }
//
//             for (int i = 0; i < friendRequests.size(); i++) {
//                 FriendRequest fr = friendRequests.get(i);
//                 Optional<User> receiver = userService.findById(fr.getReceiverId());
//                 String receiverInfo = receiver.map(u -> String.format("%s (%s)", u.getGlobalName(), u.getEmail())).orElse("알 수 없음");
//                 System.out.println((i + 1) + ". " + receiverInfo);
//             }
//
//             printGuidePrevious();
//             String idxStr = InputHandler.getInputOrBack("취소할 요청 선택 : ");
//             if (idxStr == null) {
//                 return;
//             }
//
//             try {
//                 int idx = Integer.parseInt(idxStr);
//                 if (idx < 1 || idx > friendRequests.size()) {
//                     System.out.println("유효한 번호를 입력해주세요.");
//                     continue;
//                 }
//
//                 FriendRequest selected = friendRequests.get(idx - 1);
//
//                 Boolean confirm = InputHandler.getYesOrNo("이 친구 요청을 취소하시겠습니까?");
//                 if (confirm == null) {
//                     return;
//                 }
//
//                 if (!confirm) {
//                     continue;
//                 }
//                 friendRequestService.declineFriendRequest(selected.getId());
//                 System.out.println("친구 요청을 취소했습니다.");
//             } catch (NumberFormatException e) {
//                 System.out.println("숫자를 입력해주세요.");
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//         }
//     }
//
//     private void deleteFriend() {
//         while (true) {
//             List<User> friends = userService.getFriends(me.getId()).stream().toList();
//             if (friends.isEmpty()) {
//                 System.out.println("친구 : 없음");
//                 return;
//             }
//
//             System.out.println("\n친구 목록:");
//             for (int i = 0; i < friends.size(); i++) {
//                 User f = friends.get(i);
//                 System.out.printf("%d. %s (%s, %s)\n", i + 1, f.getGlobalName(), f.getUsername(), f.getEmail());
//             }
//
//             printGuidePrevious();
//             String idxStr = InputHandler.getInputOrBack("삭제할 친구 번호: ");
//             if (idxStr == null) {
//                 return;
//             }
//
//             try {
//                 int idx = Integer.parseInt(idxStr) - 1;
//                 if (idx < 0 || idx >= friends.size()) {
//                     System.out.println("유효한 번호를 입력해주세요.");
//                     continue;
//                 }
//
//                 User friend = friends.get(idx);
//
//                 Boolean confirm = InputHandler.getYesOrNo(String.format("%s(%s)님을 정말 삭제하시겠습니까?", friend.getGlobalName(), friend.getEmail()));
//                 if (confirm == null) {
//                     return;
//                 }
//
//                 if (!confirm) {
//                     continue;
//                 }
//
//                 userService.removeFriend(me.getId(), friend.getId());
//                 System.out.println("친구가 삭제되었습니다.");
//             } catch (NumberFormatException e) {
//                 System.out.println("숫자를 입력해주세요.");
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//         }
//     }
//
//     private void showGuilds() {
//         printGuildList(guildService.findAll());
//     }
//
//     private void printGuildList(List<Guild> guilds) {
//         if (guilds == null || guilds.isEmpty()) {
//             System.out.println("\n존재하는 서버가 없습니다.");
//             return;
//         }
//
//         System.out.println("\n서버 목록 : ");
//         for (int i = 0; i < guilds.size(); i++) {
//             System.out.println((i + 1) + ". " + guilds.get(i));
//         }
//     }
//
//     private Integer selectGuildIndex(List<Guild> guilds, String prompt) {
//         while (true) {
//             String idxStr = InputHandler.getInputOrBack(prompt);
//             if (idxStr == null) {
//                 return null;
//             }
//             try {
//                 int idx = Integer.parseInt(idxStr);
//                 if (idx >= 1 && idx <= guilds.size()) {
//                     return idx - 1;
//                 }
//                 System.out.println("유효한 서버 번호를 입력해주세요.\n");
//             } catch (NumberFormatException e) {
//                 System.out.println("숫자를 입력해주세요.\n");
//             }
//         }
//     }
//
//     private void createGuild() {
//         printGuidePrevious();
//         while (true) {
//             String name = InputHandler.getInputOrBack("서버 이름 : ");
//             if (name == null || name.isBlank()) {
//                 name = userService.findById(me.getId()).orElseThrow().getUsername() + "님의 서버";
//             }
//
//             Boolean isPublic = InputHandler.getYesOrNo("공개 여부");
//             if (isPublic == null) {
//                 return;
//             }
//
//             try {
//                 Guild guild = guildService.save(new Guild(name, isPublic, me.getId()));
//                 if (guild != null) {
//                     Channel defaultChatChannel = channelService.save(new Channel(guild.getId(), "일반", ChannelType.CHAT));
//                     Channel defaultVoiceChannel = channelService.save(new Channel(guild.getId(), "일반", ChannelType.VOICE));
//                     guildService.addChannel(guild.getId(), defaultChatChannel);
//                     guildService.addChannel(guild.getId(), defaultVoiceChannel);
//                     userService.addGuild(me.getId(), guild.getId());
//                     System.out.println(guild.getName() + " 서버가 생성되었습니다.");
//                     return;
//                 }
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//             System.out.println("다시 시도해 주세요.\n");
//         }
//     }
//
//     private void deleteGuild() {
//         printGuidePrevious();
//         while (true) {
//             List<Guild> guilds = guildService.findGuildsOwnedByUser(me.getId());
//             if (guilds.isEmpty()) {
//                 System.out.println("삭제할 수 있는 서버가 없습니다.");
//                 return;
//             }
//
//             printGuildList(guilds);
//
//             Integer idx = selectGuildIndex(guilds, "삭제할 서버 번호 : ");
//             if (idx == null) {
//                 return;
//             }
//
//             Guild guild = guilds.get(idx);
//
//             if (!guild.isOwner(me.getId())) {
//                 System.out.println("삭제할 권한이 없습니다.");
//                 continue;
//             }
//
//             Boolean confirm = InputHandler.getYesOrNo("'" + guild.getName() + "' 서버를 정말 삭제할까요?");
//             if (confirm == null) {
//                 return;
//             }
//
//             if (!confirm) {
//                 System.out.println("취소되었습니다.");
//                 continue;
//             }
//             try {
//                 guildService.deleteGuild(guild.getId(), me.getId());
//                 System.out.println(guild.getName() + " 서버가 삭제되었습니다.");
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//         }
//     }
//
//     private void joinGuild() {
//         while (true) {
//             printGuidePrevious();
//
//             List<Guild> guilds = guildService.findDiscoverableGuilds();
//
//             if (guilds.isEmpty()) {
//                 System.out.println("입장 가능한 서버가 없습니다.");
//                 return;
//             }
//
//             printGuildList(guilds);
//
//             Integer idx = selectGuildIndex(guilds, "입장할 서버 번호 : ");
//             if (idx == null) {
//                 return;
//             }
//
//             Guild guild = guilds.get(idx);
//             if (guild.getMembers().containsKey(me.getId())) {
//                 System.out.println("이미 입장한 서버입니다.");
//                 continue;
//             }
//
//             try {
//                 guildService.addMember(guild.getId(), me.getId());
//                 System.out.println(guild.getName() + " 서버에 입장했습니다.");
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//         }
//     }
//
//     private void exitGuild() {
//         printGuidePrevious();
//
//         while (true) {
//             List<Guild> guilds = guildService.findGuildsJoinedByUser(me.getId());
//
//             if (guilds.isEmpty()) {
//                 System.out.println("🔍 입장한 서버가 없습니다.");
//                 return;
//             }
//
//             printGuildList(guilds);
//
//             Integer idx = selectGuildIndex(guilds, "나갈 서버 번호 : ");
//             if (idx == null) {
//                 return;
//             }
//
//             Guild guild = guilds.get(idx);
//
//             if (guild.isOwner(me.getId())) {
//                 System.out.println("서버 주인 변경 후 퇴장이 가능합니다.");
//                 continue;
//             }
//
//             try {
//                 guildService.removeMember(guild.getId(), me.getId());
//                 System.out.println(guild.getName() + " 서버에서 퇴장했습니다.");
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//         }
//     }
//
//     private void openGuild() {
//         printGuidePrevious();
//
//         List<Guild> guilds = guildService.findGuildsJoinedByUser(me.getId());
//
//         if (guilds.isEmpty()) {
//             System.out.println("🔍 입장한 서버가 없습니다.");
//             return;
//         }
//
//         printGuildList(guilds);
//
//         Integer idx = selectGuildIndex(guilds, "열 서버 번호 : ");
//         if (idx == null) {
//             return;
//         }
//
//         Guild guild = guilds.get(idx);
//         System.out.println(guild.getName() + " 서버를 열었습니다.");
//         enteredGuildId = guild.getId();
//
//         guildMenu();
//     }
//
//     private void searchGuild() {
//         printGuidePrevious();
//         while (true) {
//             String keyword = InputHandler.getInputOrBack("검색할 서버명 : ");
//             if (keyword == null) {
//                 return;
//             }
//
//             if (keyword.isBlank()) {
//                 System.out.println("검색어를 입력해주세요.");
//                 continue;
//             }
//
//             List<Guild> results = guildService.searchGuilds(keyword);
//             if (results.isEmpty()) {
//                 System.out.println("🔍 해당 조건에 맞는 서버가 없습니다.");
//                 continue;
//             }
//
//             System.out.println("\n검색 결과:");
//             results.stream().map(Guild::getName).forEach(System.out::println);
//         }
//     }
//
//     private void showGuildInfo() {
//         try {
//             Guild guild = guildService.getOrThrow(enteredGuildId);
//             System.out.println(guild);
//         } catch (Exception e) {
//             System.out.println(e.getMessage());
//         }
//     }
//
//     private Guild checkOwnershipAndReturnGuild() {
//         try {
//             Guild guild = guildService.getOrThrow(enteredGuildId);
//             if (!guild.isOwner(me.getId())) {
//                 System.out.println("권한이 없습니다.");
//                 return null;
//             }
//             return guild;
//         } catch (Exception e) {
//             System.out.println(e.getMessage());
//         }
//         return null;
//     }
//
//     private void changeGuildOwner() {
//         while (true) {
//             Guild guild = checkOwnershipAndReturnGuild();
//             if (guild == null) {
//                 return;
//             }
//
//             List<UUID> members = guild.getMembers().keySet().stream().filter(id -> !id.equals(guild.getOwnerId())).toList();
//
//             if (members.isEmpty()) {
//                 System.out.println("새로운 주인으로 지정할 멤버가 없습니다.");
//                 return;
//             }
//
//             for (int i = 0; i < members.size(); i++) {
//                 Optional<User> member = userService.findById(members.get(i));
//                 System.out.println((i + 1) + ". " + member.map(User::getEmail).orElse(members.get(i).toString()));
//             }
//
//             String memberIdxStr = InputHandler.getInputOrBack("새로운 주인(멤버) 번호 : ");
//             if (memberIdxStr == null) {
//                 return;
//             }
//
//             try {
//                 int memberIdx = Integer.parseInt(memberIdxStr);
//                 if (memberIdx < 1 || memberIdx > members.size()) {
//                     System.out.println("올바른 번호를 입력해주세요.");
//                     continue;
//                 }
//
//                 UUID newOwnerId = members.get(memberIdx - 1);
//                 if (newOwnerId.equals(me.getId())) {
//                     System.out.println("이미 서버 주인입니다.");
//                     continue;
//                 }
//
//                 guildService.updateOwnerId(guild.getId(), me.getId(), newOwnerId);
//                 System.out.println("서버 주인이 변경되었습니다 : " + userService.getOrThrow(newOwnerId).getUsername());
//             } catch (NumberFormatException e) {
//                 System.out.println("숫자를 입력해주세요.");
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//         }
//     }
//
//     private void changeGuildName() {
//         while (true) {
//             Guild guild = checkOwnershipAndReturnGuild();
//             if (guild == null) {
//                 return;
//             }
//
//             printGuidePrevious();
//             System.out.println("현재 이름 : " + guild.getName());
//
//             String guildName = InputHandler.getInputOrBack("변경할 이름 : ");
//             if (guildName == null) {
//                 return;
//             }
//
//             if (guildName.isBlank()) {
//                 guildName = userService.findById(me.getId()).orElseThrow().getUsername() + "님의 서버";
//             }
//
//             try {
//                 guildService.updateName(guild.getId(), guildName);
//                 System.out.println("서버 이름이 변경되었습니다: " + guildName);
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//         }
//     }
//
//     private void changeGuildPublic() {
//         while (true) {
//             Guild guild = checkOwnershipAndReturnGuild();
//             if (guild == null) {
//                 return;
//             }
//
//             printGuidePrevious();
//             System.out.println("현재 공개 여부 : " + (guild.isDiscoverable() ? "공개" : "비공개"));
//
//             Boolean isDiscoverable = InputHandler.getYesOrNo("공개 여부");
//             if (isDiscoverable == null) {
//                 return;
//             }
//
//             if (guild.isDiscoverable() == isDiscoverable) {
//                 System.out.println("변경된 내용이 없습니다.");
//                 continue;
//             }
//
//             guildService.updateDiscoverable(guild.getId(), isDiscoverable);
//         }
//     }
//
//     private void kickMember() {
//         while (true) {
//             Guild guild = checkOwnershipAndReturnGuild();
//             if (guild == null) {
//                 return;
//             }
//
//             List<UUID> memberList = guild.getMembers().keySet().stream().filter(id -> !id.equals(guild.getOwnerId())).toList();
//
//             if (memberList.isEmpty()) {
//                 System.out.println("추방할 멤버가 없습니다.");
//                 return;
//             }
//
//             System.out.println("회원 목록:");
//             for (int i = 0; i < memberList.size(); i++) {
//                 Optional<User> user = userService.findById(memberList.get(i));
//                 System.out.println((i + 1) + ". " + user.map(User::getEmail).orElse(memberList.get(i).toString()));
//             }
//
//             String indexStr = InputHandler.getInputOrBack("추방할 멤버 번호 : ");
//             if (indexStr == null) {
//                 return;
//             }
//
//             try {
//                 int idx = Integer.parseInt(indexStr) - 1;
//
//                 if (idx < 0 || idx >= memberList.size()) {
//                     throw new NumberFormatException("올바른 번호를 입력해주세요.");
//                 }
//
//                 UUID memberId = memberList.get(idx);
//
//                 String username = userService.getOrThrow(memberId).getUsername();
//                 guildService.removeMember(guild.getId(), memberId);
//                 userService.removeGuild(memberId, guild.getId());
//                 System.out.println(username + " 멤버가 추방되었습니다.");
//             } catch (NumberFormatException e) {
//                 System.out.println("숫자를 입력해주세요.");
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//         }
//     }
//
//     private void showChannels() {
//         try {
//             Guild guild = guildService.getOrThrow(enteredGuildId);
//             List<Channel> channels = guild.getChannels();
//             if (channels.isEmpty()) {
//                 System.out.println("채널 없음");
//                 return;
//             }
//             int i = 1;
//             for (Channel ch : channels) {
//                 System.out.println(i++ + ". " + ch);
//             }
//         } catch (Exception e) {
//             System.out.println(e.getMessage());
//         }
//     }
//
//     private void createChannel() {
//         Guild guild = checkOwnershipAndReturnGuild();
//         if (guild == null) {
//             return;
//         }
//
//         while (true) {
//             try {
//                 String name = InputHandler.getInputOrBack("채널 이름 : ");
//                 if (name == null) {
//                     return;
//                 }
//
//                 System.out.println("채널 유형 : ");
//                 for (int i = 0; i < ChannelType.values().length; i++) {
//                     System.out.println((i + 1) + ". " + ChannelType.values()[i]);
//                 }
//
//                 int typeIdx = InputHandler.getMenuInput(ChannelType.values().length, "채널 유형 선택 : ");
//
//                 if (typeIdx < 1 || typeIdx > ChannelType.values().length) {
//                     throw new NumberFormatException();
//                 }
//
//                 ChannelType type = ChannelType.values()[typeIdx - 1];
//
//                 Channel newChannel = new Channel(guild.getId(), name, type);
//                 guildService.addChannel(guild.getId(), newChannel);
//                 System.out.println("채널이 생성되었습니다.");
//                 break;
//             } catch (NumberFormatException e) {
//                 System.out.println("올바른 번호를 입력해주세요");
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//         }
//     }
//
//     private void updateChannel() {
//         Guild guild = checkOwnershipAndReturnGuild();
//         if (guild == null) {
//             return;
//         }
//
//         List<Channel> channels = guild.getChannels();
//         if (channels == null || channels.isEmpty()) {
//             System.out.println("채널 없음");
//             return;
//         }
//
//         printGuidePrevious();
//         showChannels();
//         while (true) {
//             String idxStr = InputHandler.getInputOrBack("수정할 채널 번호 : ");
//             if (idxStr == null) {
//                 return;
//             }
//
//             try {
//                 int idx = Integer.parseInt(idxStr) - 1;
//                 if (idx < 0 || idx >= channels.size()) {
//                     throw new NumberFormatException();
//                 }
//
//                 Channel channel = channels.get(idx);
//
//                 System.out.println("현재 채널 이름 : " + channel.getName());
//                 final String newName = InputHandler.getInputOrBack("새 채널 이름 : ");
//
//                 System.out.println("현재 채널 유형 : " + channel.getType());
//                 System.out.print("새 채널 유형 : ");
//                 System.out.println("1. 채팅");
//                 System.out.println("2. 음성");
//                 System.out.println("3. 포럼");
//                 String typeIdxStr = InputHandler.getInputOrBack("새 채널 번호 : ");
//                 if (typeIdxStr == null) {
//                     return;
//                 }
//
//                 int typeIdx = Integer.parseInt(typeIdxStr);
//                 if (typeIdx < 1 || typeIdx > 3) {
//                     throw new NumberFormatException();
//                 }
//
//                 ChannelType type = ChannelType.values()[typeIdx - 1];
//
//                 if (newName != null && !newName.isBlank()) {
//                     channel.setName(newName);
//                 }
//                 //
//                 System.out.println("채널이 수정되었습니다.");
//             } catch (NumberFormatException e) {
//                 System.out.println("올바른 번호를 입력해주세요");
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//         }
//     }
//
//     private void deleteChannel() {
//         Guild guild = checkOwnershipAndReturnGuild();
//         if (guild == null) {
//             return;
//         }
//
//         List<Channel> channels = guild.getChannels();
//         if (channels == null || channels.isEmpty()) {
//             System.out.println("채널 없음");
//             return;
//         }
//
//         printGuidePrevious();
//         showChannels();
//         while (true) {
//             String idxStr = InputHandler.getInputOrBack("삭제할 채널 번호 : ");
//             if (idxStr == null) {
//                 return;
//             }
//
//             try {
//                 int idx = Integer.parseInt(idxStr) - 1;
//                 if (idx < 0 || idx >= channels.size()) {
//                     throw new NumberFormatException();
//                 }
//
//                 Channel channel = channels.get(idx);
//
//                 guildService.removeChannel(guild.getId(), channel);
//                 System.out.println("채널이 삭제되었습니다.");
//                 break;
//             } catch (NumberFormatException e) {
//                 System.out.println("올바른 번호를 입력해주세요.");
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//         }
//     }
//
//     private void createChatRoom() {
//         List<User> selectedParticipants = new ArrayList<>();
//         List<User> friends = new ArrayList<>(userService.getFriends(me.getId()));
//
//         if (friends.isEmpty()) {
//             System.out.println("친구 없음");
//             return;
//         }
//
//         while (true) {
//             System.out.println("\n선택할 친구 목록:");
//             for (int i = 0; i < friends.size(); i++) {
//                 User f = friends.get(i);
//                 System.out.printf("%d. %s (%s, %s)\n", i + 1, f.getGlobalName(), f.getUsername(), f.getEmail());
//             }
//             System.out.println("\n선택된 친구 목록:");
//             for (User f : selectedParticipants) {
//                 System.out.printf("%s (%s, %s)\n", f.getGlobalName(), f.getUsername(), f.getEmail());
//             }
//
//             printGuidePrevious();
//             String idxStr = InputHandler.getInputOrBack("선택할 친구 번호(0: 완료): ");
//             if (idxStr == null) {
//                 return;
//             }
//             if (idxStr.equals("0")) {
//                 break;
//             }
//
//             try {
//                 int idx = Integer.parseInt(idxStr) - 1;
//
//                 if (idx < 0 || idx >= friends.size()) {
//                     System.out.println("유효한 번호를 입력해주세요.");
//                     continue;
//                 }
//
//                 User chosen = friends.get(idx);
//                 selectedParticipants.add(chosen);
//                 friends.remove(chosen);
//             } catch (NumberFormatException e) {
//                 System.out.println("숫자를 입력해주세요.");
//             } catch (Exception e) {
//                 System.out.println(e.getMessage());
//             }
//
//             if (selectedParticipants.isEmpty()) {
//                 System.out.println("1명 이상을 선택해주세요.");
//                 return;
//             }
//
//             selectedParticipants.add(me);
//             try {
//                 ChatRoom chatRoom = chatRoomService.save(new ChatRoom(selectedParticipants.stream().map(User::getId).collect(Collectors.toSet())));
//                 if (chatRoom != null) {
//                     for (User u : selectedParticipants) {
//                         u.addChatRoom(chatRoom.getId());
//                     }
//                     System.out.println("대화방이 생성되었습니다.");
//                 }
//             } catch (NoSuchElementException | IllegalStateException e) {
//                 System.out.println(e.getMessage());
//             } catch (Exception e) {
//                 System.out.println("알 수 없는 오류: " + e.getMessage());
//             }
//         }
//     }
//
//     public void sendMessageToChatRoom() {
//         while (true) {
//             printGuidePrevious();
//             List<UUID> myChatRoomIds = userService.findById(me.getId()).orElseThrow().getChatRooms().stream().toList();
//
//             if (myChatRoomIds.isEmpty()) {
//                 System.out.println("참여 중인 채팅방이 없습니다.");
//                 return;
//             }
//
//             System.out.println("\n📂 참여 중인 채팅방 목록:");
//             for (int i = 0; i < myChatRoomIds.size(); i++) {
//                 Optional<ChatRoom> chatRoom = chatRoomService.findById(myChatRoomIds.get(i));
//
//                 if (chatRoom.isEmpty()) {
//                     continue;
//                 }
//
//                 List<String> participantsNames = chatRoomService.getParticipantNames(chatRoom.get().getId());
//
//                 System.out.printf("%2d. %s\n", i + 1, String.join(", ", participantsNames));
//             }
//
//             UUID selectedRoomId;
//             while (true) {
//                 String idxStr = InputHandler.getInputOrBack("메시지를 보낼 채팅방 번호: ");
//                 if (idxStr == null) {
//                     return;
//                 }
//                 try {
//                     int idx = Integer.parseInt(idxStr) - 1;
//                     if (idx < 0 || idx >= myChatRoomIds.size()) {
//                         System.out.println("유효한 번호를 입력해주세요.");
//                         continue;
//                     }
//                     selectedRoomId = myChatRoomIds.get(idx);
//                     break;
//                 } catch (NumberFormatException e) {
//                     System.out.println("숫자를 입력해주세요.");
//                 } catch (Exception e) {
//                     System.out.println(e.getMessage());
//                 }
//             }
//
//             String content;
//             while (true) {
//                 content = InputHandler.getInputOrBack("보낼 메시지를 입력하세요: ");
//                 if (content == null) {
//                     return;
//                 }
//
//                 if (!content.isBlank()) {
//                     break;
//                 }
//
//                 System.out.println("메시지를 입력해주세요.");
//             }
//
//             Message message = messageService.save(new Message(me.getId(), content, List.of()));
//             chatRoomService.addMessage(selectedRoomId, message.getId());
//             System.out.println("\nDM 히스토리: ");
//             chatRoomService.printMessages(selectedRoomId);
//         }
//     }
// }
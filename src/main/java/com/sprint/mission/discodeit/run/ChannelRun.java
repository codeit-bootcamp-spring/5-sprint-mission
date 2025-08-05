package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ChannelRun {
    private final Scanner sc;
    private final ChannelService channelService;
    private final UserService userService;

    public ChannelRun(Scanner sc, ChannelService channelService, UserService userService) {
        this.sc = sc;
        this.channelService = channelService;
        this.userService = userService;
    }

    public void run() throws Exception {
        while (true) {
            System.out.println("\n[채널 서비스 메뉴]");
            System.out.println("1. 채널 등록");
            System.out.println("2. 채널 조회");
            System.out.println("3. 전체 채널 조회");
            System.out.println("4. 채널 수정");
            System.out.println("5. 채널 삭제");
            System.out.println("9. 뒤로 가기");
            System.out.print("선택: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
                continue;
            }

            switch (choice) {
                case 1 -> createChannel();
                case 2 -> getChannel();
                case 3 -> getAllChannels();
                case 4 -> updateChannel();
                case 5 -> deleteChannel();
                case 9 -> {
                    System.out.println("메인 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void createChannel() throws Exception {
        System.out.print("채널명: ");
        String name = sc.nextLine().trim();

        System.out.print("본인 이름: ");
        String userName = sc.nextLine().trim();
        User user = userService.get(userName);
        if (user == null) {
            System.out.println("존재하지 않는 사용자입니다.");
            return;
        }
        if (!passwordMatch(user)) return;
        if (channelService.get(name) != null) {
            System.out.println("이미 존재하는 채널명입니다.");
            return;
        }
        Channel newChannel = new Channel(name, user.getId());
        channelService.create(newChannel);
        System.out.println("등록 완료:\n[채널명: " + newChannel.getName() + "]\n[작성자: " + userName + "]");
    }

    private void getChannel() throws Exception {
        System.out.print("조회할 채널명: ");
        String name = sc.nextLine().trim();
        Channel channel = channelService.get(name);
        if (channel == null) {
            System.out.println("해당 이름의 채널 없음");
            return;
        }
        System.out.println("채널 정보:\n" + channel);
    }

    private void getAllChannels() throws Exception{
        List<Channel> channels = channelService.getAll();
        if (channels.isEmpty()) {
            System.out.println("등록된 채널 없음");
            return;
        }
        System.out.println("\n<전체 채널 목록>");
        for (Channel c : channels) {
            System.out.println("-------------------------------------------");
            System.out.println(c);
        }
        System.out.println("-------------------------------------------");
    }

    private void updateChannel() throws Exception{
        System.out.print("수정할 채널명: ");
        String name = sc.nextLine().trim();
        Channel channel = channelService.get(name);
        if (channel == null) {
            System.out.println("해당 이름의 채널 없음");
            return;
        }

        User user = userService.get(channel.getCreatorUserId());
        if (user == null) {
            System.out.println("작성자 정보가 없습니다.");
            return;
        }

        System.out.print("본인 이름: ");
        String userName = sc.nextLine().trim();
        if (!userName.equals(user.getName())) {
            System.out.println("작성자만 수정할 수 있습니다.");
            return;
        }

        if (!passwordMatch(user)) return;

        System.out.print("새 채널명: ");
        String newName = sc.nextLine();
        channel.update(newName);
        channelService.update(channel);
        System.out.println("수정 완료");
    }

    private void deleteChannel() throws Exception{
        System.out.print("삭제할 채널명: ");
        String name = sc.nextLine().trim();
        Channel channel = channelService.get(name);
        if (channel == null) {
            System.out.println("해당 이름의 채널 없음");
            return;
        }

        User user = userService.get(channel.getCreatorUserId());
        if (user == null) {
            System.out.println("작성자 정보가 없습니다.");
            return;
        }

        System.out.print("본인 이름: ");
        String userName = sc.nextLine().trim();
        if (!userName.equals(user.getName())) {
            System.out.println("작성자만 삭제할 수 있습니다.");
            return;
        }

        if (!passwordMatch(user)) return;

        channelService.delete(channel.getId());
        System.out.println("삭제 완료");
    }

    private boolean passwordMatch(User user) {
        System.out.print("비밀번호 확인: ");
        String pw = sc.nextLine();
        if (!user.getPassword().equals(pw)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return false;
        }
        return true;
    }
}

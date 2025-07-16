package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ChannelRun {
    private final Scanner sc;
    private final ChannelService channelService;

    public ChannelRun(Scanner sc, ChannelService channelService) {
        this.sc = sc;
        this.channelService = channelService;
    }

    public void run() {
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
                case 1:
                    createChannel();
                    break;
                case 2:
                    getChannel();
                    break;
                case 3:
                    getAllChannels();
                    break;
                case 4:
                    updateChannel();
                    break;
                case 5:
                    deleteChannel();
                    break;
                case 9:
                    System.out.println("메인 메뉴로 돌아갑니다.");
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void createChannel() {
        System.out.print("채널명: ");
        String name = sc.nextLine();
        Channel newChannel = new Channel(name);
        channelService.create(newChannel);
        System.out.println("등록 완료:\n[채널명: " + newChannel.getName() + "]");
    }

    private void getChannel() {
        System.out.println("조회할 채널명: ");
        try {
            String name = sc.nextLine().trim();
            Channel channel = channelService.get(name);
            if (channel == null) {
                System.out.println("해당 ID의 채널 없음");
                return;
            }
            System.out.println("채널 정보: " + channel);
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않은 UUID 형식입니다.");
        }
    }

    private void getAllChannels() {
        List<Channel> channels = channelService.getAll();
        if (channels.isEmpty()) {
            System.out.println("등록된 채널 없음");
            return;
        }
        System.out.println("전체 채널 목록");
        for (Channel c : channels){
            System.out.print("-------------------------------------------");
            System.out.println(c.toString());
            System.out.print("-------------------------------------------");
        }
        System.out.println();
    }

    private void updateChannel() {
        System.out.println(("수정할 채널명: "));
        try {
            String name = sc.nextLine().trim();
            Channel channel = channelService.get(name);
            if (channel == null) {
                System.out.println("해당 이름의 채널 없음");
                return;
            }
            System.out.println("새 채널명: ");
            String newName = sc.nextLine();
            channel.update(newName);
            channelService.update(channel);
            System.out.println("수정 완료");
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않은 UUID 형식입니다.");
        }
    }

    private void deleteChannel() {
        System.out.print("삭제할 채널명: ");
        try {
            String name = sc.nextLine().trim();
            Channel channel = channelService.get(name);

            if(channel == null) {
                System.out.println("해당 ID의 채널 없음");
                return;
            }

            UUID channelID = channel.getId();
            channelService.delete(channelID);
            System.out.println("삭제 완료");
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않은 UUID 형식입니다.");
        }
    }
}

package com.sprint.mission.discodeit.main;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.run.ChannelRun;
import com.sprint.mission.discodeit.run.MessageRun;
import com.sprint.mission.discodeit.run.UserRun;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.io.IOException;
import java.util.Scanner;

public class JavaApplication {
    public static void main(String[] args){
        //JCF 서비스 인스턴스 가져오기
//      UserService userService = JCFUserService.getInstance();
//      ChannelService channelService = JCFChannelService.getInstance();
//      MessageService messageService = JCFMessageService.getInstance();

        //파일 서비스 인스턴스 가져오기
        UserService userService = FileUserService.getInstance();
        ChannelService channelService = FileChannelService.getInstance();
        MessageService messageService = FileMessageService.getInstance();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== 서비스 선택 =====");
            System.out.println("1. 유저 서비스");
            System.out.println("2. 채널 서비스");
            System.out.println("3. 메시지 서비스");
            System.out.println("9. 종료");
            System.out.print("선택: ");

            int menu;
            try {
                menu = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
                continue;
            }

            try {
                switch (menu) {
                    case 1:
                        new UserRun(sc, userService).run();
                        break;
                    case 2:
                        new ChannelRun(sc, channelService, userService).run();
                        break;
                    case 3:
                        new MessageRun(sc, messageService, userService, channelService).run();
                        break;
                    case 9:
                        System.out.println("프로그램 종료");
                        return;
                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            } catch (Exception e) {
                GlobalExceptionHandler.handle(e);
            }
        }
    }
}

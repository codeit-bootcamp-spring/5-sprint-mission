package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Application {

    public static void main(String[] args) {
//        userType();
//        ChannelType();
//         MessageType();


//        userFileIoTest();
//        channelFileIOTest();
//        messageFileIoTest();


    }




    static void messageFileIoTest() {
        System.out.println("-----------------------");
        Map<UUID, User> userData = new HashMap<>();

        User user01 = new User("홍길동", "123456");
        User user02 = new User("김길동", "532132");

        userData.put(user01.getId(), user01);
        userData.put(user02.getId(), user02);

        Map<UUID, Channel> channelData = new HashMap<>();
        Channel channel01 = new Channel("00022", "공부 대화방");
        channelData.put(channel01.getId(), channel01);
        FileMessageService fileMessageService = new FileMessageService(userData, channelData);
        Message message01 = new Message("안녕하세요", user01.getUserName(), user02.getUserName());
        fileMessageService.create(message01);

        Message message02 = new Message("반가워요", user02.getUserName(), user01.getUserName());
        fileMessageService.create(message02);
        System.out.println("--------------------");
        try {
            Message messageError = new Message("에러메시지", "에러이름", " 에러이름02");
            fileMessageService.create(messageError);
        } catch (IllegalArgumentException e) {
            System.out.println("메세지 생성 실패 : 유저 이름이 다름");
        }
        System.out.println("--------------------");


        System.out.println("Message : 등록완료");


        Message messageFind01 = fileMessageService.find(message01.getId());
        System.out.println("조회 단건 : " + messageFind01.getMessage() + "  보낸이 :" + messageFind01.getSender());

        Message messageFind02 = fileMessageService.find(message02.getId());
        System.out.println("조회 단건 : " + messageFind02.getMessage() + "  보낸이 :" + messageFind02.getSender());

        System.out.println("--------------------");


        ArrayList<Message> list = fileMessageService.allFind();
        for (int i = 0; i < list.size(); i++) {
            System.out.println("다건 전채 검색 userid " + (i + 1) + "  번쨰 " + list.get(i).getMessage() + " 보낸이 " + list.get(i).getSender());
        }
        System.out.println("--------------------");


        //첫번째 id 변경

        message01.update("두번쟤 인사입니다.", System.currentTimeMillis());
        message02.update("저도 두번 반갑습니다 .", System.currentTimeMillis());


        //수정된 데이터 조회
        ArrayList<Message> list02 = fileMessageService.allFind();
        for (int i = 0; i < list02.size(); i++) {
            System.out.println("수정된 uuid " + (i + 1) + "  번쨰  " + list02.get(i).getMessage());
        }
        System.out.println("--------------------");
        //삭제
        fileMessageService.delete(message01.getId());
        ArrayList<Message> list03 = fileMessageService.allFind();

        if (list03.isEmpty()) {
            System.out.println("삭제후 전체 데이터 확인  아무것도 없음 ");
            return;
        }

        for (int i = 0; i < list03.size(); i++) {
            System.out.println("삭제후 남은 메세지..... " + list03.get(i).getMessage());
        }

    }

    static void MessageType() {
        System.out.println("--------------------");


        Map<UUID, User> userData = new HashMap<>();

        User user01 = new User("홍길동", "123456");
        User user02 = new User("김길동", "532132");

        userData.put(user01.getId(), user01);
        userData.put(user02.getId(), user02);

        Map<UUID, Channel> channelData = new HashMap<>();
        Channel channel01 = new Channel("00022", "공부 대화방");
        channelData.put(channel01.getId(), channel01);


        JCFMessageService messageService = new JCFMessageService(userData, channelData);
        Message message01 = new Message("안녕하세요", user01.getUserName(), user02.getUserName());
        messageService.create(message01);

        Message message02 = new Message("반가워요", user02.getUserName(), user01.getUserName());
        messageService.create(message02);
        System.out.println("--------------------");
        try {
            Message messageError = new Message("에러메시지", "에러이름", " 에러이름02");
            messageService.create(messageError);
        } catch (IllegalArgumentException e) {
            System.out.println("메세지 생성 실패 : 유저 이름이 다름");
        }
        System.out.println("--------------------");


        System.out.println("Message : 등록완료");


        Message messageFind01 = messageService.find(message01.getId());
        System.out.println("조회 단건 : " + messageFind01.getMessage() + "  보낸이 :" + messageFind01.getSender());

        Message messageFind02 = messageService.find(message02.getId());
        System.out.println("조회 단건 : " + messageFind02.getMessage() + "  보낸이 :" + messageFind02.getSender());

        System.out.println("--------------------");


        ArrayList<Message> list = messageService.allFind();
        for (int i = 0; i < list.size(); i++) {
            System.out.println("다건 전채 검색 userid " + (i + 1) + "  번쨰 " + list.get(i).getMessage() + " 보낸이 " + list.get(i).getSender());
        }
        System.out.println("--------------------");


        //첫번째 id 변경

        message01.update("두번쟤 인사입니다.", System.currentTimeMillis());
        message02.update("저도 두번 반갑습니다 .", System.currentTimeMillis());


        //수정된 데이터 조회
        ArrayList<Message> list02 = messageService.allFind();
        for (int i = 0; i < list02.size(); i++) {
            System.out.println("수정된 uuid " + (i + 1) + "  번쨰  " + list02.get(i).getMessage());
        }
        System.out.println("--------------------");
        //삭제
        messageService.delete(message01.getId());
        ArrayList<Message> list03 = messageService.allFind();

        if (list03.isEmpty()) {
            System.out.println("삭제후 전체 데이터 확인  아무것도 없음 ");
            return;
        }

        for (int i = 0; i < list03.size(); i++) {
            System.out.println("삭제후 남은 메세지..... " + list03.get(i).getMessage());
        }

    }

    static void ChannelType() {
        JCFChannelService channelService = new JCFChannelService();

        Channel channel01 = new Channel("001", "공부채널");
        Channel channel02 = new Channel("002", "수업채널");

        channelService.create(channel01);
        channelService.create(channel02);

        System.out.println("Channel : 등록완료");

        Channel channelFind01 = channelService.find(channel01.getId());
        System.out.println("조회 단건 channel name01 : " + channelFind01.getChannelName());
        Channel channelFind02 = channelService.find(channel02.getId());
        System.out.println("조회 단건 channel name02 : " + channelFind02.getChannelName());

        System.out.println("--------------------");


        ArrayList<Channel> list = channelService.allFind();
        for (int i = 0; i < list.size(); i++) {
            System.out.println("다건 전채 검색 userid " + (i + 1) + "  번쨰 " + list.get(i).getChannelName());
        }
        System.out.println("--------------------");

        channel01.update("003", "수정된 공부 채널", System.currentTimeMillis());
        channel02.update("004", "수정된 수업 채널", System.currentTimeMillis());


        //수정된 데이터 조회
        ArrayList<Channel> list02 = channelService.allFind();
        for (int i = 0; i < list02.size(); i++) {
            System.out.println("수정된 uuid " + (i + 1) + "  번쨰  " + list02.get(i).getChannelName());
        }
        System.out.println("--------------------");
        //삭제
        channelService.delete(channel01.getId());
        ArrayList<Channel> list03 = channelService.allFind();
        for (Channel channel : list03) {
            System.out.println("삭제후 전체 데이터 확인   " + channel.getChannelName());
        }


    }

    static void channelFileIOTest() {

        FileChannelService fileChannelService = new FileChannelService();
        Channel channel01 = new Channel("001", "공부채널");
        Channel channel02 = new Channel("002", "수업채널");

        fileChannelService.create(channel01);
        fileChannelService.create(channel02);

        System.out.println("Channel : 등록완료");

        Channel channelFind01 = fileChannelService.find(channel01.getId());
        System.out.println("조회 단건 channel name01 : " + channelFind01.getChannelName());
        Channel channelFind02 = fileChannelService.find(channel02.getId());
        System.out.println("조회 단건 channel name02 : " + channelFind02.getChannelName());

        System.out.println("--------------------");


        ArrayList<Channel> list = fileChannelService.allFind();
        for (int i = 0; i < list.size(); i++) {
            System.out.println("다건 전채 검색 userid " + (i + 1) + "  번쨰 " + list.get(i).getChannelName());
        }
        System.out.println("--------------------");

        channel01.update("003", "수정된 공부 채널", System.currentTimeMillis());
        channel02.update("004", "수정된 수업 채널", System.currentTimeMillis());


        //수정된 데이터 조회
        ArrayList<Channel> list02 = fileChannelService.allFind();
        for (int i = 0; i < list02.size(); i++) {
            System.out.println("수정된 uuid " + (i + 1) + "  번쨰  " + list02.get(i).getChannelName());
        }
        System.out.println("--------------------");
        //삭제
        fileChannelService.delete(channel01.getId());
        ArrayList<Channel> list03 = fileChannelService.allFind();
        for (Channel channel : list03) {
            System.out.println("삭제후 전체 데이터 확인   " + channel.getChannelName());
        }

    }


    static void userFileIoTest() {
        FileUserService fileService = new FileUserService();

        User user01 = new User("홍길동", "123456");
        User user02 = new User("김철수", "123456");
        User user03 = new User("이수진", "123456");

        fileService.create(user01);
        fileService.create(user02);
        fileService.create(user03);

        System.out.println("저장완료");
        System.out.println("--------------------");

        User userFind01 = fileService.find(user01.getId());
        System.out.println("조회 단건 user01 uuid : " + userFind01.getUserName());

        User userFind02 = fileService.find(user02.getId());
        System.out.println("조회 단건 user02 uuid : " + userFind02.getUserName());

        User userFind03 = fileService.find(user03.getId());
        System.out.println("조회 단건 user03 uuid : " + userFind03.getUserName());


        System.out.println("--------------------");
        ArrayList<User> list = fileService.allFind();
        for (int i = 0; i < list.size(); i++) {
            System.out.println("다건 전채 검색 userid " + (i + 1) + "  번쨰 " + list.get(i).getUserName());
        }
        System.out.println("--------------------");

        fileService.update(user01.getId(), new User("강호동", "64221"));
        fileService.update(user02.getId(), new User("이수근", "64221"));


        //수정된 데이터 조회
        ArrayList<User> list02 = fileService.allFind();
        for (int i = 0; i < list02.size(); i++) {
            System.out.println("수정된  " + (i + 1) + "  번쨰  " + list02.get(i).getUserName());
        }
        System.out.println("--------------------");
        //삭제
        fileService.delete(user01.getId());
        fileService.delete(user02.getId());
        ArrayList<User> list03 = fileService.allFind();
        for (User user : list03) {
            System.out.println("삭제후 전체 데이터 확인   " + user.getUserName());
        }


    }

    static void userType() {

        JCFUserService userService = new JCFUserService();

        User user01 = new User("홍길동", "123456");
        User user02 = new User("이길동", "1234567");
        User user03 = new User("삼길동", "123456");
        User user04 = new User("사길동", "1234567");

        userService.create(user01);
        userService.create(user02);
        userService.create(user03);
        userService.create(user04);

        System.out.println("User : 등록완료");

        //조회 단건
        User userFind01 = userService.find(user01.getId());
        System.out.println("조회 단건 user01 uuid : " + userFind01.getUserName());

        User userFind02 = userService.find(user02.getId());
        System.out.println("조회 단건 user02 uuid : " + userFind02.getUserName());

        User userFind03 = userService.find(user03.getId());
        System.out.println("조회 단건 user03 uuid : " + userFind03.getUserName());

        User userFind04 = userService.find(user04.getId());
        System.out.println("조회 단건 user04 uuid : " + userFind04.getUserName());

        System.out.println("--------------------");
        ArrayList<User> list = userService.allFind();
        for (int i = 0; i < list.size(); i++) {
            System.out.println("다건 전채 검색 userid " + (i + 1) + "  번쨰 " + list.get(i).getUserName());
        }
        System.out.println("--------------------");
        //다른 uuid 로 수정..

        userService.update(user01.getId(), new User("강호동", "64221"));
        userService.update(user02.getId(), new User("이수근", "64221"));


        //수정된 데이터 조회
        ArrayList<User> list02 = userService.allFind();
        for (int i = 0; i < list02.size(); i++) {
            System.out.println("수정된  " + (i + 1) + "  번쨰  " + list02.get(i).getUserName());
        }
        System.out.println("--------------------");
        //삭제
        userService.delete(user01.getId());
        userService.delete(user02.getId());
        ArrayList<User> list03 = userService.allFind();
        for (User user : list03) {
            System.out.println("삭제후 전체 데이터 확인   " + user.getUserName());
        }

    }


}

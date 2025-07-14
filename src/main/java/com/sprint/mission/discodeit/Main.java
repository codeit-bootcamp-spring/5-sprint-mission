package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.ArrayList;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
      //  userType();
       // ChannelType();
        MessageType();
    }

    static  void MessageType(){
        JCFMessageService messageService = new JCFMessageService();
        UUID id01 = UUID.randomUUID();
        UUID id02 = UUID.randomUUID();
        Message message01 = new Message(id01);
        Message message02 = new Message(id02);

        messageService.save(message01);
        messageService.save( message02);
        System.out.println("Message : 등록완료");

        Message messageFind01 = messageService.find(id01);
        System.out.println("조회 단건 message uuid01 : " + messageFind01.getId());
        Message messageFind02 = messageService.find(id02);
        System.out.println("조회 단건 message uuid02 : " + messageFind02.getId());

        System.out.println("--------------------");


        ArrayList<Message> list = messageService.allFind();
        for (int i = 0; i< list.size(); i++) {
            System.out.println("다건 전채 검색 userid " + (i+1) +"  번쨰 " + list.get(i).getId());
        }
        System.out.println("--------------------");
        //다른 uuid 로 수정..

        UUID id03 = UUID.randomUUID();
        UUID id04 = UUID.randomUUID();

        //첫번째 id 변경
        message01.update(id03 , System.currentTimeMillis());
        messageService.update(id01, message01);

        //두번쨰 id 번경

        message02.update(id04, System.currentTimeMillis());
        messageService.update(id02, message02);

        //수정된 데이터 조회
        ArrayList<Message> list02 = messageService.allFind();
        for (int i = 0; i< list02.size(); i++) {
            System.out.println("수정된 uuid " + (i+1) +"  번쨰  " + list02.get(i).getId());
        }
        System.out.println("--------------------");
        //삭제
        messageService.delete(id01);
        ArrayList<Message> list03 = messageService.allFind();
        for (Message message : list03) {
            System.out.println("삭제후 전체 데이터 확인   " + message.getId());
        }

    }

    static  void ChannelType(){
        JCFChannelService  channelService = new JCFChannelService();
        UUID id01 = UUID.randomUUID();
        UUID id02 = UUID.randomUUID();
        Channel channel01 = new Channel(id01);
        Channel channel02 = new Channel(id02);

        channelService.save(channel01);
        channelService.save( channel02);
        System.out.println("Channel : 등록완료");

        Channel channelFind01 = channelService.find(id01);
        System.out.println("조회 단건 channel uuid01 : " + channelFind01.getId());
        Channel channelFind02 = channelService.find(id02);
        System.out.println("조회 단건 channel uuid02 : " + channelFind02.getId());

        System.out.println("--------------------");


        ArrayList<Channel> list = channelService.allFind();
        for (int i = 0; i< list.size(); i++) {
            System.out.println("다건 전채 검색 userid " + (i+1) +"  번쨰 " + list.get(i).getId());
        }
        System.out.println("--------------------");
        //다른 uuid 로 수정..

        UUID id03 = UUID.randomUUID();
        UUID id04 = UUID.randomUUID();

        //첫번째 id 변경
        channel01.update(id03 , System.currentTimeMillis());
        channelService.update(id01, channel01);

        //두번쨰 id 번경

        channel02.update(id04, System.currentTimeMillis());
        channelService.update(id02, channel02);

        //수정된 데이터 조회
        ArrayList<Channel> list02 = channelService.allFind();
        for (int i = 0; i< list02.size(); i++) {
            System.out.println("수정된 uuid " + (i+1) +"  번쨰  " + list02.get(i).getId());
        }
        System.out.println("--------------------");
        //삭제
        channelService.delete(id01);
        ArrayList<Channel> list03 = channelService.allFind();
        for (Channel channel : list03) {
            System.out.println("삭제후 전체 데이터 확인   " + channel.getId());
        }


    }

    static void userType(){

        JCFUserService userService = new JCFUserService();
        UUID id01 = UUID.randomUUID();
        UUID id02 = UUID.randomUUID();
        User user01 = new User(id01);
        User user02 = new User(id02);

        userService.save(user01);
        userService.save(user02);

        System.out.println("User : 등록완료");

        //조회 단건
        User userFind01 = userService.find(id01);
        System.out.println("조회 단건 user01 uuid : " + userFind01.getId());

        User userFind02 = userService.find(id02);
        System.out.println("조회 단건 user02 uuid : " + userFind02.getId());
        System.out.println("--------------------");
        ArrayList<User> list = userService.allFind();
        for (int i = 0; i< list.size(); i++) {
            System.out.println("다건 전채 검색 userid " + (i+1) +"  번쨰 " + list.get(i).getId());
        }
        System.out.println("--------------------");
        //다른 uuid 로 수정..

        UUID id03 = UUID.randomUUID();
        UUID id04 = UUID.randomUUID();

        //첫번째 id 변경
        user01.update(id03 , System.currentTimeMillis());
        userService.update(id01, user01);

        //두번쨰 id 번경

        user02.update(id04, System.currentTimeMillis());
        userService.update(id02, user02);

        //수정된 데이터 조회
        ArrayList<User> list02 = userService.allFind();
        for (int i = 0; i< list02.size(); i++) {
            System.out.println("수정된 uuid " + (i+1) +"  번쨰  " + list02.get(i).getId());
        }
        System.out.println("--------------------");
        //삭제
        userService.delete(id01);
        ArrayList<User> list03 = userService.allFind();
        for (User user : list03) {
            System.out.println("삭제후 전체 데이터 확인   " + user.getId());
        }

    }


}

package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {

        /*
        * Map을 이용한 JCF(java collection framework) 기반의 서비스 구현체
        * Channel, Message, User 객체 각각을
        * 생성 -> 조회 -> 수정 -> 삭제 테스트하는 코드
        * */


        /*Channel 테스트*/

        //1. 채널 서비스 구현체 생성
        ChannelService channelService = new JCFChannelService();

        //2. 채널 객체 생성 및 등록
        Channel channel = new Channel(); //uuid 자동 생성
        channelService.create(channel); //map에 저장
        System.out.println("채널 등록 ID: " + channel.getId()); //생성자에서 만든 uuid

        //3. 등록한 채널 ID 단건 조회
        Channel foundChannel = channelService.findById(channel.getId());
        System.out.println("채널 단건 조회:" + foundChannel.getId());

        //4. 전체 조회 (List 형태로 반환)
        System.out.println("채널 전체 조회" + channelService.findAll());

        //5. 수정
        foundChannel.updateTime(); //수정 시간만 바꾸기
        channelService.update(foundChannel); //Map에 덮어쓰기

        //6. 수정된 데이터 확인
        System.out.println("수정 후 조회: " + channelService.findById(channel.getId()).getUpdateAt());

        //7. 삭제
        channelService.delete(channel.getId());

        //8. 삭제 확인
        System.out.println("삭제 후 조회해보기: " + channelService.findById(channel.getId()));


        /*Message 테스트*/

        //1. 메세지 서비스 구현체 생성
        MessageService messageService = new JCFMessageService();

        //2. 메세지 객체 생성 및 등록
        Message message = new Message();
        messageService.create(message);
        System.out.println("message 등록: ID: " + message.getId());

        //3. ID로 단건 조회
        Message foundMessage = messageService.findById(message.getId());
        System.out.println("단건 메세지 조회: " + foundMessage.getId());

        //4. 전체조회(모든 메세지)
        System.out.println("전체 메세지 조회: " + messageService.findAll());

        //5. 수정
        foundMessage.updateTime(); //수정 시간만 바꾸기
        messageService.update(foundMessage); //다시 Map에 저장

        //6. 수정결과 출력해보기
        System.out.println("메세지 수정 후 조회: " + foundMessage); //객체 toString 출력

        //7. 삭제
        messageService.delete(message);

        //8. 삭제 확인
        System.out.println("메세지 삭제 후 조회: " + messageService.findById(message.getId()));

        /*User 테스트*/

        //1. 유저 서비스 구현체 생성
        UserService userService = new JCFUserService();

        //2. 유저 객체 생성 및 등록
        User user = new User(); // 매개변수가 아무것도 없는 기본 생성자 호출
        userService.create(user);
        System.out.println("유저 등록: " + user.getId());

        //3. 단건 조회
        User foundUser = userService.findById(user.getId());
        System.out.println("유저 단건 조회: " + foundUser.getId());

        //4. 전체 조회
        System.out.println("전체 조회: " + userService.findAll());

        //5. 수정
        foundUser.updateTime(); //수정 시간만 바꾸기
        userService.update(foundUser); //Map에 반영

        //6. 수정 확인
        System.out.println("수정 후 유저 조회: " + userService.findById(user.getId()));

        //7. 삭제
        userService.delete(user.getId());

        //8. 삭제 확인
        System.out.println("삭제 후 조회: " + userService.findById(user.getId()));

    }
}

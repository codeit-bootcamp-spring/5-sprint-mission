package com.sprint.mission.discodeit.main;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.jcf.JCFChannelService;
import com.sprint.mission.discodeit.jcf.JCFMessageService;
import com.sprint.mission.discodeit.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    static JCFUserService jcfUserService = new JCFUserService();
    static JCFChannelService jcfChannelService = new JCFChannelService();
    static JCFMessageService jcfMessageService = new JCFMessageService(jcfUserService, jcfChannelService);

    public static void main(String[] args) {
        testUserService();
        testChannelService();
        testMessageService();
    }

    //JCFUserService Test
    public static void testUserService() {
        System.out.println("=============================");
        System.out.println("[사용자 등록]");
        //사용자 등록 성공 ("사용자 : {name} 등록 성공")
        User user1 = jcfUserService.register(new User("1111", "홍길동"));
        User user2 = jcfUserService.register(new User("2222", "박길동"));
        User user3 = jcfUserService.register(new User("3333", "이길동"));
        User user4 = jcfUserService.register(new User("4444", "김철수"));
        User user5 = jcfUserService.register(new User("5555", "김영희"));
        //user6 사용자 등록 실패 (예외 발생 - "사용자 등록에 실패했습니다")
//        User user6 =  jcfUserService.register(new User("6666", " "));

        System.out.println("\n=============================");
        System.out.println("[사용자 조회 (단건)]");
        //user1 사용자 조회 성공
        User result1 = jcfUserService.findById(user1.getId());
        System.out.println(result1);
        //랜덤 사용자 조회 실패 (예외 발생 - "사용자에서 해당 {id}를 찾을 수 없습니다")
//        User result2 = jcfUserService.findById(UUID.randomUUID());


        System.out.println("\n=============================");
        System.out.println("[사용자 조회 (다건)]");
        //등록된 모든 사용자 출력
        jcfUserService.findAll().forEach(System.out::println);

        System.out.println("\n=============================");
        System.out.println("[사용자 비밀번호 수정 조회]");
        //user3 사용자 비밀번호 수정 성공 ("사용자 비밀번호가 수정되었습니다")
        User result3 = jcfUserService.update(user3.getId(), "1234");
        System.out.println(printUpdatedResult(result3));
        //수정 실패 - 새로운 비밀번호 입력 안함 (예외 발생 - "새로운 비밀번호를 입력하세요.")
//        User result4 = jcfUserService.update(user3.getId(), "");
        //수정 실패 (예외 발생 - "사용자에서 해당 {id}를 찾을 수 없습니다")
//        User result4 = jcfUserService.update(UUID.randomUUID(), "1234");

        System.out.println("\n=============================");
        System.out.println("[사용자 삭제 확인]");
        //user4 삭제 성공 ("사용자가 삭제되었습니다")
        User result5 = jcfUserService.delete(user4.getId());
        printDeleteResult(result5);
        //랜덤 사용자 삭제 실패 (예외 발생 - "사용자에서 해당 {id}를 찾을 수 없습니다")
//        User result6 = jcfUserService.findById(UUID.randomUUID());
    }

    public static void testMessageService() {
        System.out.println("=============================");
        System.out.println("[메시지 등록]");
        //메시지 등록 성공 ("메시지 : {content} 등록 성공")
        Message message1 = jcfMessageService.register(new Message("안녕하세요", jcfUserService.findAll().get(0).getId(), jcfChannelService.findAll().get(0).getId()));
        Message message2 = jcfMessageService.register(new Message("반갑습니다", jcfUserService.findAll().get(1).getId(), jcfChannelService.findAll().get(1).getId()));
        Message message3 = jcfMessageService.register(new Message("잘부탁드려요", jcfUserService.findAll().get(2).getId(), jcfChannelService.findAll().get(1).getId()));
        Message message4 = jcfMessageService.register(new Message("안녕히 계세요", jcfUserService.findAll().get(2).getId(), jcfChannelService.findAll().get(2).getId()));
        Message message5 = jcfMessageService.register(new Message("건강하세요", jcfUserService.findAll().get(2).getId(), jcfChannelService.findAll().get(2).getId()));
        //message6 메시지 등록 실패 (예외 발생 - "메시지 등록에 실패했습니다.")
        //Message message6 = jcfMessageService.register(new Message(" ", jcfUserService.findAll().get(2).getId(), jcfChannelService.findAll().get(2).getId()));
        //message7 메시지 등록 실패 (예외 발생 - "사용자에서 해당 {id}를 찾을 수 없습니다.")
        //Message message7 = jcfMessageService.register(new Message("행복하세요", UUID.randomUUID(), jcfChannelService.findAll().get(2).getId()));
        //message8 메시지 등록 실패 (예외 발생 - "채널에서 해당 {id}를 찾을 수 없습니다")
        //Message message8 = jcfMessageService.register(new Message("감사합니다", jcfUserService.findAll().get(2).getId(), UUID.randomUUID()));

        System.out.println("\n=============================");
        System.out.println("[메시지 조회 (단건)]");
        //message1 메시지 조회 성공
        Message result1 = jcfMessageService.findById(message1.getId());
        System.out.println(result1);
        //랜덤 메시지 조회 실패 (예외 발생 - "메시지에서 해당 {id}를 찾을 수 없습니다")
//        Message result2 = jcfMessageService.findById(UUID.randomUUID());

        System.out.println("\n=============================");
        System.out.println("[메시지 조회 (다건)]");
        //등록된 모든 메시지 출력
        jcfMessageService.findAll().forEach(System.out::println);

        System.out.println("\n=============================");
        System.out.println("[메시지 내용 수정 조회]");
        //message2 메시지 내용 수정 성공 ("메시지 내용이 수정되었습니다.")
        Message result3 = jcfMessageService.update(message2.getId(), "1234");
        System.out.println(printUpdatedResult(result3));
        //수정 실패 - 새로운 메시지 입력 안함 (예외 발생 - "새로운 메시지 내용을 입력하세요.")
//        Message result4= jcfMessageService.update(message2.getId(), "");
        //수정 실패 - 새로운 메시지 입력 안함 (예외 발생 - "메시지에서 해당 {id}를 찾을 수 없습니다")
//        Message result4 = jcfMessageService.update(UUID.randomUUID(), "1234");

        System.out.println("\n=============================");
        System.out.println("[메시지 삭제 확인]");
        //message3 삭제 성공 ("메시지가 삭제되었습니다")
        Message result5 = jcfMessageService.delete(message3.getId());
        printDeleteResult(result5);
        //랜덤 메시지 삭제 실패 (예외 발생 - "메시지에서 해당 {id}를 찾을 수 없습니다")
//        Message result6 = jcfMessageService.findById(UUID.randomUUID());
    }

    public static void testChannelService() {
        System.out.println("=============================");
        System.out.println("[채널 등록]");
        //채널등록 성공 ("채널 : {name} 등록 성공")
        Channel channel1 = jcfChannelService.register(new Channel("수다방", "떠들어요"));
        Channel channel2 = jcfChannelService.register(new Channel("친목방", "친해져요"));
        Channel channel3 = jcfChannelService.register(new Channel("게임", "다같이 게임해요"));
        Channel channel4 = jcfChannelService.register(new Channel("운동", "다같이 운동해요"));
        //채널 등록 실패 (예외 발생 - "메시지 등록에 실패했습니다")
        //Channel channel5 = jcfChannelService.register(new Channel(" ", "~~~"));

        System.out.println("\n=============================");
        System.out.println("[채널 조회 (단건)]");
        //channel1 조회 성공
        Channel result1 = jcfChannelService.findById(channel1.getId());
        System.out.println(result1);
        //랜덤 채널 조회 실패 (예외 발생 - "채널에서 해당 {id}를 찾을 수 없습니다")
//        Channel result2 = jcfChannelService.findById(UUID.randomUUID());

        System.out.println("\n=============================");
        System.out.println("[채널 조회 (다건)]");
        //등록된 모든 채널 춫ㄹ력
        jcfChannelService.findAll().forEach(System.out::println);

        System.out.println("\n=============================");
        System.out.println("[채널 설명 수정 조회]");
        //channel2 채널 설명 수정 성공 ("채널 설명이 수정되었습니다.")
        Channel result3 = jcfChannelService.update(channel2.getId(), "친목방입니다");
        System.out.println(printUpdatedResult(result3));
        //랜덤 채널 설명 수정 실패 (예외 발생 : "채널에서 해당 {id}를 찾을 수 없습니다.")
//        Channel result4= jcfChannelService.update(UUID.randomUUID(), "안녕");
        //수정 실패 - 새로운 채널 설명 입력 안함 (예외 발생 - "새로운 채널 설명을 입력하세요.")
//        Channel result5= jcfChannelService.update(channel2.getId(), " ");

        System.out.println("\n=============================");
        System.out.println("[채널 삭제 확인]");
        //channel4 삭제 성공 ("채널이 삭제되었습니다.")
        Channel result5 = jcfChannelService.delete(channel4.getId());
        printDeleteResult(result5);
        //랜덤 채널 삭제 실패 ("채널에서 해당 {id}를 찾을 수 없습니다.")
//        Channel result6 = jcfChannelService.findById(UUID.randomUUID());
    }


    /**
     * 엔티티 종류에 따라 수정 성공 메시지 출력
     * @param result
     * @return
     */
    public static String printUpdatedResult(Object result) {
        if (result instanceof User) return "사용자 비밀번호가 수정되었습니다.";
        if (result instanceof Message) return "메시지 내용이 수정되었습니다.";
        if (result instanceof Channel) return "채널 설명이 수정되었습니다.";
        return "";
    }

    /**
     * 삭제 결과 출력
     * @param result
     */
    public static void printDeleteResult(Object result) {
        if (result instanceof User) {
            System.out.println("사용자가 삭제되었습니다");
            jcfUserService.findAll().forEach(System.out::println);
        } else if (result instanceof Message) {
            System.out.println("메시지가 삭제되었습니다");
            jcfMessageService.findAll().forEach(System.out::println);
        } else if (result instanceof Channel) {
            System.out.println("채널이 삭제되었습니다");
            jcfChannelService.findAll().forEach(System.out::println);
        }
    }
}

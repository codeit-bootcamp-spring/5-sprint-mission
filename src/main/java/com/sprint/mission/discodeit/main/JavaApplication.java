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
    static JCFMessageService jcfMessageService = new JCFMessageService();
    static JCFChannelService jcfChannelService = new JCFChannelService();

    public static void main(String[] args) {
        testUserService();
        testChannelService();
        testMessageService();
    }

    //JCFUserService Test
    public static void testUserService() {
        System.out.println("=============================");
        System.out.println("[사용자 등록]");
        User user1 = jcfUserService.register(new User("1111", "홍길동"));
        User user2 = jcfUserService.register(new User("2222", "박길동"));
        User user3 = jcfUserService.register(new User("3333", "이길동"));
        User user4 = jcfUserService.register(new User("4444", "김철수"));
        User user5 = jcfUserService.register(new User("5555", "김영희"));

        System.out.println("\n=============================");
        System.out.println("[사용자 조회 (단건)]");
        User result1 = jcfUserService.findById(user1.getId());
        printFindResult(result1);
        User result2 = jcfUserService.findById(UUID.randomUUID());
        printFindResult(result2);

        System.out.println("\n=============================");
        System.out.println("[사용자 조회 (다건)]");
        jcfUserService.findAll().forEach(System.out::println);

        System.out.println("\n=============================");
        System.out.println("[사용자 비밀번호 수정 조회]");
        User result3 = jcfUserService.update(user3.getId(), "1234");
        printUpdateResult(result3);
        User result4= jcfUserService.update(user3.getId(), "");
        printUpdateResult(result4);

        System.out.println("\n=============================");
        System.out.println("[사용자 삭제 확인]");
        User result5 =  jcfUserService.delete(user4.getId());
        printDeleteResult(result5);
        User result6 = jcfUserService.findById(UUID.randomUUID());
        printDeleteResult(result6);
    }

    public static void testMessageService() {
        System.out.println("=============================");
        System.out.println("[메시지 등록]");
        Message message1 = jcfMessageService.register(new Message("안녕하세요", jcfUserService.findAll().get(0).getId(), jcfChannelService.findAll().get(0).getId()));
        Message message2 = jcfMessageService.register(new Message("반갑습니다", jcfUserService.findAll().get(1).getId(), jcfChannelService.findAll().get(1).getId()));
        Message message3 = jcfMessageService.register(new Message("잘부탁드려요", jcfUserService.findAll().get(2).getId(), jcfChannelService.findAll().get(1).getId()));
        Message message4 = jcfMessageService.register(new Message("안녕히 계세요", jcfUserService.findAll().get(2).getId(), jcfChannelService.findAll().get(2).getId()));
        Message message5 = jcfMessageService.register(new Message("건강하세요", jcfUserService.findAll().get(2).getId(), jcfChannelService.findAll().get(2).getId()));

        System.out.println("\n=============================");
        System.out.println("[메시지 조회 (단건)]");
        Message result1 = jcfMessageService.findById(message1.getId());
        printFindResult(result1);
        Message result2 = jcfMessageService.findById(UUID.randomUUID());
        printFindResult(result2);

        System.out.println("\n=============================");
        System.out.println("[메시지 조회 (다건)]");
        jcfMessageService.findAll().forEach(System.out::println);

        System.out.println("\n=============================");
        System.out.println("[메시지 내용 수정 조회]");
        Message result3 = jcfMessageService.update(message2.getId(), "1234");
        printUpdateResult(result3);
        Message result4= jcfMessageService.update(message2.getId(), "");
        printUpdateResult(result4);

        System.out.println("\n=============================");
        System.out.println("[메시지 삭제 확인]");
        Message result5 =  jcfMessageService.delete(message3.getId());
        printDeleteResult(result5);
        Message result6 = jcfMessageService.findById(UUID.randomUUID());
        printDeleteResult(result6);
    }

    public static void testChannelService() {
        System.out.println("=============================");
        System.out.println("[채널 등록]");
        Channel channel1 = jcfChannelService.register(new Channel("수다방", "떠들어요"));
        Channel channel2 = jcfChannelService.register(new Channel("친목방", "친해져요"));
        Channel channel3 = jcfChannelService.register(new Channel("게임", "다같이 게임해요"));
        Channel channel4 = jcfChannelService.register(new Channel("운동", "다같이 운동해요"));

        System.out.println("\n=============================");
        System.out.println("[채널 조회 (단건)]");
        Channel result1 = jcfChannelService.findById(channel1.getId());
        printFindResult(result1);
        Channel result2 = jcfChannelService.findById(UUID.randomUUID());
        printFindResult(result2);

        System.out.println("\n=============================");
        System.out.println("[채널 조회 (다건)]");
        jcfChannelService.findAll().forEach(System.out::println);

        System.out.println("\n=============================");
        System.out.println("[채널 설명 수정 조회]");
        Channel result3 = jcfChannelService.update(channel2.getName(), "친목방입니다");
        printUpdateResult(result3);
        Channel result4= jcfChannelService.update(" ", "안녕");
        printUpdateResult(result4);

        System.out.println("\n=============================");
        System.out.println("[채널 삭제 확인]");
        Channel result5 =  jcfChannelService.delete(channel4.getName());
        printDeleteResult(result5);
        Channel result6 = jcfChannelService.findById(UUID.randomUUID());
        printDeleteResult(result6);
    }

    /**
     * 조회 결과 출력
     * @param result
     * @param <T>
     */
    public static <T> void printFindResult (T result){
        if (result != null)
            System.out.println(result.toString());
        else
            System.out.println("찾을 수 없습니다");
    }


    /**
     * 수정 결과 출력
     * @param result
     * @param <T>
     */
    public static <T> void printUpdateResult (T result){
        if (result != null){
            if (result instanceof User)
                System.out.println("사용자의 비밀번호가 수정되었습니다");
            else if (result instanceof Message)
                System.out.println("메시지 내용이 수정되었습니다");
            else if (result instanceof Channel)
                System.out.println("채널 설명이 수정되었습니다");
            System.out.println(result);
        }
        else
            System.out.println("수정에 실패했습니다");
    }

    /**
     * 삭제 결과 출력
     * @param result
     * @param <T>
     */
    public static <T> void printDeleteResult (T result){
        if (result != null){
            if (result instanceof User) {
                System.out.println("사용자가 삭제되었습니다");
                jcfUserService.findAll().forEach(System.out::println);
            }
            else if (result instanceof Message) {
                System.out.println("메시지가 삭제되었습니다");
                jcfMessageService.findAll().forEach(System.out::println);
            }
            else if (result instanceof Channel) {
                System.out.println("채널이 삭제되었습니다");
                jcfChannelService.findAll().forEach(System.out::println);
            }
        }
        else
            System.out.println("삭제에 실패했습니다\n");
    }
}

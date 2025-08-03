package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.NoSuchElementException;

public class MessageServiceTest {
    private MessageService messageService;
    private UserService userService;
    private ChannelService channelService;

    public void runAllTest() {
        beforeEach();
        save();

        beforeEach();
        findOne();

        beforeEach();
        findAll();

        beforeEach();
        update();

        beforeEach();
        delete();
    }

    public void beforeEach() {
        messageService = new JCFMessageService();
        userService = new JCFUserService();
        channelService = new JCFChannelService();
    }

    public void save() {
        printLine("save");

        User user = userService.save(new User("홍길동", "길동2", "1234"));
        Channel channel = channelService.save(new Channel("소통해요", "소통방입니다"));

        Message message = messageService.save(new Message(channel.getId(), user.getId(), "채팅 테스트"));

        boolean success = channel.getId().equals(message.getChannelId())
                && user.getId().equals(message.getAuthorId())
                && "채팅 테스트".equals(message.getContent());

        printResult("save", success);
    }

    public void findOne() {
        printLine("findOne");

        User user = userService.save(new User("홍길동", "길동2", "1234"));
        Channel channel = channelService.save(new Channel("소통해요", "소통방입니다"));

        Message message = messageService.save(new Message(channel.getId(), user.getId(), "채팅 테스트"));

        Message findMessage = messageService.findById(message.getId());

        printResult("findOne", message.equals(findMessage));
    }

    public void findAll() {
        printLine("findAll");

        User user = userService.save(new User("홍길동", "길동2", "1234"));
        Channel channel = channelService.save(new Channel("소통해요", "소통방입니다"));

        List<String> messages = List.of("채팅 테스트1", "채팅 테스트2", "채팅 테스트3");
        long savedCount = messages.stream()
                .map(content -> new Message(channel.getId(), user.getId(), content))
                .map(messageService::save)
                .count();

        printResult("findAll", savedCount == messages.size());
    }

    private void update() {
        printLine("update");

        User user = userService.save(new User("홍길동", "길동2", "1234"));
        Channel channel = channelService.save(new Channel("소통해요", "소통방입니다"));

        Message message = messageService.save(new Message(channel.getId(), user.getId(), "채팅 테스트"));

        messageService.update(message.getId(), new Message(message.getChannelId(), message.getAuthorId(), "내용 수정"));

        boolean isSuccess = "내용 수정".equals(message.getContent());

        printResult("update", isSuccess);
    }

    private void delete() {
        printLine("delete");

        User user = userService.save(new User("홍길동", "길동2", "1234"));
        Channel channel = channelService.save(new Channel("소통해요", "소통방입니다"));
        Message message = messageService.save(new Message(channel.getId(), user.getId(), "채팅 테스트"));

        messageService.delete(message.getId());

        try {
            messageService.findById(message.getId());
        } catch (NoSuchElementException e) {
            printResult("delete", true);
            return;
        }

        printResult("delete", false);
    }

    public void printLine(String methodName) {
        System.out.println("======= Message " + methodName + " test =======");
    }

    public void printResult(String testName, boolean success) {
        if (success) {
            System.out.println("[" + testName + "] passed\n");
        } else {
            System.err.println("[" + testName + "] failed\n");
        }
    }
}

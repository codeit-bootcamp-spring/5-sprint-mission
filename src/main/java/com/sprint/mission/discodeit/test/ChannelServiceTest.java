package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;

public class ChannelServiceTest {
    private final ChannelService channelService;

    public ChannelServiceTest(ChannelService channelService) {
        this.channelService = channelService;
    }

    public void runAllTest() {
        save();
        afterEach();

        findOne();
        afterEach();

        findAll();
        afterEach();

        update();
        afterEach();

        delete();
        afterEach();
    }

    public void afterEach() {
        channelService.deleteAll();
    }

    public void save() {
        printLine("save");

        Channel channel = new Channel("질문방", "질문 있으면 하는 방");

        Channel savedChannel = channelService.save(channel);


        printResult("save", channel.equals(savedChannel));
    }

    public void findOne() {
        printLine("findOne");

        Channel channel = new Channel("질문방", "질문 있으면 하는 방");

        channelService.save(channel);
        Channel findChannel = channelService.findById(channel.getId());

        printResult("findOne", channel.equals(findChannel));
    }

    public void findAll() {
        printLine("findAll");

        Channel channel1 = new Channel("질문방", "질문 있으면 하는 방");
        Channel channel2 = new Channel("오늘-뭐-먹지", "#점메추#저메추");
        Channel channel3 = new Channel("소개합니다", "간단히 소개 부탁드립니다.");

        channelService.save(channel1);
        channelService.save(channel2);
        channelService.save(channel3);

        List<Channel> allChannels = channelService.findAll();

        printResult("findAll", allChannels.size() == 3);
    }

    public void update() {
        printLine("update");

        Channel channel = new Channel("질문방", "질문 있으면 하는 방");
        channelService.save(channel);

        channelService.update(channel.getId(), new Channel("Q&A", "공지 봐주세요"));

        Channel updateChannel = channelService.findById(channel.getId());

        boolean isSuccess = "Q&A".equals(updateChannel.getName())
                && "공지 봐주세요".equals(updateChannel.getDescription());

        printResult("update", isSuccess);
    }

    public void delete() {
        printLine("delete");

        Channel channel = new Channel("질문방", "질문 있으면 하는 방");
        channelService.save(channel);
        channelService.delete(channel.getId());

        try {
            channelService.findById(channel.getId());
        } catch (NoSuchElementException e) {
            printResult("delete", true);
            return;
        }

        printResult("delete", false);
    }

    public void printLine(String methodName) {
        System.out.println("======= Channel " + methodName + " test =======");
    }

    public void printResult(String testName, boolean success) {
        if (success) {
            System.out.println("[" + testName + "] passed\n");
        } else {
            System.err.println("[" + testName + "] failed\n");
        }
    }
}

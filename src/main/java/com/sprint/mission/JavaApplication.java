package com.sprint.mission;

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
import java.util.Set;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) throws InterruptedException {
        UserService us = JCFUserService.getInstance();
        ChannelService cs = JCFChannelService.getInstance();
        MessageService ms = JCFMessageService.getInstnce();

        User user1 = new User("홍길동");
        User user2 = new User("이순신");
        User user3 = new User("김유신");

        us.create(user1);
        us.create(user2);
        us.create(user3);

        // 채널
        Channel c1 = new Channel("일반채널");
        Channel c2 = new Channel("공지사항");
        Channel c3 = new Channel("개발잡담");

        cs.create(c1);
        cs.create(c2);
        cs.create(c3);

        // 유저 채널
        c1.join(user1.getId());
        c2.join(user2.getId());
        c3.join(user3.getId());

        Message m1 = new Message(user3.getId(), c2.getId(), "공지사항에 오신 걸 환영합니다.");
        Message m2 = new Message(user3.getId(), c2.getId(), "오늘 회의는 2시에 시작합니다.");
        ms.create(m1);
        ms.create(m2);

        Channel notice = cs.findById(c2.getId());
        if (notice != null) {
            // 유저 목록 출력
            System.out.println( c2.getName() + " 채널 유저 목록");
            Set<UUID> userIds = notice.getUserIds();
            for (UUID userId : userIds) {
                User u = us.findById(userId);
                if (u != null) {
                    System.out.println("- " + u.getNickName());
                }
            }

            System.out.println( c2.getName() + " 메세지 출력");
            List<Message> msg = ms.findByChannelId(notice.getId());
            for (Message m : msg) {
                User sender = us.findById(m.getUserId());
                String nickName = sender != null ? sender.getNickName() : "Unknown";
                System.out.println(nickName + ": " + m.getContent());
            }
        }














    }
}

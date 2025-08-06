package com.sprint.mission;

import com.sprint.mission.discodeit.service.ChannelService; // ChannelService 인터페이스 import
import com.sprint.mission.discodeit.service.MessageService; // MessageService 인터페이스 import
import com.sprint.mission.discodeit.service.UserService; // UserService 인터페이스 import

import com.sprint.mission.discodeit.service.file.FileChannelService; // FileChannelService 구현체 import
import com.sprint.mission.discodeit.service.file.FileMessageService; // FileMessageService 구현체 import
import com.sprint.mission.discodeit.service.file.FileUserService; // FileUserService 구현체 import

import com.sprint.mission.discodeit.service.jcf.JCFChannelService; // JCFChannelService 구현체 import
import com.sprint.mission.discodeit.service.jcf.JCFMessageService; // JCFMessageService 구현체 import
import com.sprint.mission.discodeit.service.jcf.JCFUserService; // JCFUserService 구현체 import


//서비스 구현체 인스턴스를 제공하는 팩토리 클래스입니다.
//싱글톤 패턴을 적용하여 애플리케이션 전반에 걸쳐 단 하나의 인스턴스만 존재하도록 합니다.
//또한, 팩토리 패턴을 활용하여 서비스 간의 의존성을 관리하고 주입합니다.

public class ServiceFactory {

    // ServiceFactory의 유일한 인스턴스를 저장하는 필드 (싱글톤 패턴)
    private static ServiceFactory instance;

    // 각 서비스의 인스턴스를 저장하는 필드
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;


    //ServiceFactory의 private 생성자입니다.
    //외부에서 직접 인스턴스 생성을 막고, 싱글톤 패턴을 강제합니다.
    //이 생성자 내에서 각 서비스 구현체들을 초기화하고, 의존성을 주입합니다.

    private ServiceFactory() {
        //JCF 기반 서비스 구현체 초기화
        //this.userService = new JCFUserService();
        //this.channelService = new JCFChannelService();
        // MessageService는 UserService와 ChannelService에 의존하므로, 생성자에서 주입합니다.
        //this.messageService = new JCFMessageService(userService, channelService);

        //File 기반 서비스로 교체
        this.userService = new FileUserService();
        this.channelService = new FileChannelService();
        this.messageService = new FileMessageService(userService, channelService);
    }


    //ServiceFactory의 유일한 인스턴스를 반환하는 정적 메서드입니다.
    //(싱글톤 패턴)
    //@return ServiceFactory의 인스턴스

    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory(); // 인스턴스가 없으면 새로 생성
        }
        return instance; // 기존 인스턴스 반환
    }


    //UserService 인스턴스를 반환합니다.
    //@return UserService 인스턴스

    public UserService getUserService() {
        return userService;
    }


    //ChannelService 인스턴스를 반환합니다.
    //@return ChannelService 인스턴스

    public ChannelService getChannelService() {
        return channelService;
    }


    //MessageService 인스턴스를 반환합니다.
    //@return MessageService 인스턴스

    public MessageService getMessageService() {
        return messageService;
    }
}

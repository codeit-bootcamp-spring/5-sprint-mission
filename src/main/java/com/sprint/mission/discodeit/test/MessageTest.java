package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class MessageTest {
    Scanner sc = new Scanner(System.in);
    JCFMessageService messageService = new JCFMessageService();

    public void messageMenu() {
        while (true) {
            System.out.println("----------------------------");
            System.out.println("1. 등록");
            System.out.println("2. 조회");
            System.out.println("3. 수정");
            System.out.println("4. 삭제");
            System.out.println("5. 종료");
            System.out.print("번호: ");
            int num = sc.nextInt();

            switch (num) {
                case 1:
                    create();
                    break;
                case 2:
                    read();
                    break;
                case 3:
                    update();
                    break;
                case 4:
                    delete();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("잘못 입력하셨습니다.");
                    break;
            }
        }

    }

    public void create() {
        System.out.println("----------------------------");
        System.out.print("이름: ");
        String name = sc.next();
        sc.nextLine();
        System.out.print("제목: ");
        String title = sc.next();
        sc.nextLine();
        System.out.print("내용: ");
        String content = sc.next();
        sc.nextLine();

        Message message = messageService.createMessage(name, title, content);
        System.out.println("생성 완료! " + message);
    }

    public void read() {
        System.out.println("----------------------------");
        System.out.println("1. 읽기");
        System.out.println("2. 모두 읽기");
        System.out.println("3. 메뉴로 돌아가기");
        System.out.print("번호: ");
        int num = sc.nextInt();

        switch (num) {
            case 1:
                readOne();
                break;
            case 2:
                readAll();
                break;
            case 3:
                break;
            default:
                System.out.println("존재하지 않는 메뉴입니다.");
                break;
        }
    }

    public void readOne() {
        System.out.print("ID: ");
        String str = sc.next();
        sc.nextLine();

        UUID id;

        try {
            id = UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않습니다.");
            return;
        }

        Message message = messageService.readMessage(id);

        if(message != null) {
            System.out.println(message.getId() + " " + message.getName() + " " + message.getTitle()
                    + " " + message.getContent());
        } else {
            System.out.println("존재하지 않는 메시지입니다.");
        }
    }

    public void readAll() {
        List<Message> messageList = messageService.readAllMessages();

        if (messageList != null) {
            for (Message message : messageList) {
                System.out.println(message.getId() + " " + message.getName() + " " + message.getTitle()
                        + " " + message.getContent());
            }
        } else {
            System.out.println("등록된 메시지가 없습니다.");
        }
    }

    public void update() {
        System.out.println("----------------------------");
        System.out.println("1. 이름 변경");
        System.out.println("2. 제목 변경");
        System.out.println("3. 내용 변경");
        System.out.println("4. 메뉴로 돌아가기");
        System.out.print("번호: ");
        int num = sc.nextInt();

        switch (num) {
            case 1:
                nameUpdate();
                break;
            case 2:
                titleUpadte();
                break;
            case 3:
                contentUpdate();
                break;
            case 4:
                break;
            default:
                System.out.println("존재하지 않는 메뉴입니다.");
                break;
        }
    }

    public void nameUpdate() {
        System.out.print("ID: ");
        String str = sc.next();
        sc.nextLine();

        UUID id;
        try {
            id = UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않습니다.");
            return;
        }

        System.out.print("변경 이름: ");
        String name = sc.next();
        sc.nextLine();

        Message message = messageService.updateName(id, name);

        if (message != null) {
            System.out.println("변경 완료! " + message);
        } else {
            System.out.println("존재하지 않는 메세지입니다.");
        }
    }

    public void titleUpadte() {
        System.out.print("ID: ");
        String str = sc.next();
        sc.nextLine();

        UUID id;
        try {
            id = UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않습니다.");
            return;
        }

        System.out.print("변경 제목: ");
        String title = sc.next();
        sc.nextLine();

        Message message = messageService.updateTitle(id, title);

        if (message != null) {
            System.out.println("변경 완료! " + message);
        } else {
            System.out.println("존재하지 않는 메세지입니다.");
        }
    }

    public void contentUpdate() {
        System.out.print("ID: ");
        String str = sc.next();
        sc.nextLine();

        UUID id;
        try {
            id = UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않습니다.");
            return;
        }

        System.out.print("변경 내용: ");
        String content = sc.next();
        sc.nextLine();

        Message message = messageService.updateContent(id, content);

        if (message != null) {
            System.out.println("변경 완료! " + message);
        } else {
            System.out.println("존재하지 않는 메세지입니다.");
        }
    }

    public void delete() {
        System.out.println("----------------------------");
        System.out.print("ID: ");
        String str = sc.next();
        sc.nextLine();

        UUID id;

        try {
            id = UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않습니다.");
            return;
        }

        if (messageService.deleteMessage(id)) {
            System.out.println("삭제되었습니다.");
        } else {
            System.out.println("존재하지 않는 메세지입니다.");
        }
    }


    public static void main(String[] args) {
        new MessageTest().messageMenu();
    }
}

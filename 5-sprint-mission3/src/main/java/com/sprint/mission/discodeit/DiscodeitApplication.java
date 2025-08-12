package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = ctx.getBean(UserService.class);
        ChannelService channelService = ctx.getBean(ChannelService.class);
        MessageService messageService = ctx.getBean(MessageService.class);

        Scanner sc = new Scanner(System.in);
        System.out.println("📂 FILE-MODE Discodeit CLI (0: Exit)");

        while (true) {
            printMainMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> userMenu(sc, userService);
                case "2" -> channelMenu(sc, channelService);
                case "3" -> messageMenu(sc, messageService);
                case "0" -> {
                    System.out.println("Good-bye!");
                    ctx.close();
                    return;
                }
                default -> System.out.println("❗ Invalid choice");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n=== MAIN ===");
        System.out.println("1) User");
        System.out.println("2) Channel");
        System.out.println("3) Message");
        System.out.println("0) Exit");
        System.out.print("> ");
    }

    private static void userMenu(Scanner sc, UserService userService) {
        System.out.println("\n[User] 1:Create 2:View 3:List 4:Delete 0:Back");
        System.out.print("> ");
        switch (sc.nextLine().trim()) {
            case "1" -> createUser(sc, userService);
            case "2" -> viewUser(sc, userService);
            case "3" -> listUsers(userService);
            case "4" -> deleteUser(sc, userService);
            case "0" -> {
            }
            default -> System.out.println("❗ Invalid choice");
        }
    }

    private static void createUser(Scanner sc, UserService userService) {
        System.out.print("Enter nickname, email, password separated by space: ");
        String[] parts = sc.nextLine().split("\\s+");
        if (parts.length < 3) {
            System.out.println("❗ Please provide three values: nickname email password");
            return;
        }
        var user = userService.create(
                new UserCreateRequest(parts[0], parts[1], parts[2]), Optional.empty());
        System.out.println("✅ Created: " + user);
    }

    private static void viewUser(Scanner sc, UserService userService) {
        System.out.print("Enter userId: ");
        try {
            UUID id = UUID.fromString(sc.nextLine());
            UserDto dto = userService.find(id);
            System.out.println(dto);
        } catch (IllegalArgumentException e) {
            System.out.println("❗ Invalid UUID format");
        }
    }

    private static void listUsers(UserService userService) {
        List<UserDto> users = userService.findAll();
        if (users.isEmpty()) System.out.println("No users found.");
        else users.forEach(System.out::println);
    }

    private static void deleteUser(Scanner sc, UserService userService) {
        System.out.print("Enter userId to delete: ");
        try {
            UUID id = UUID.fromString(sc.nextLine());
            userService.delete(id);
            System.out.println("🗑️ Deleted");
        } catch (IllegalArgumentException e) {
            System.out.println("❗ Invalid UUID format");
        }
    }

    private static void channelMenu(Scanner sc, ChannelService channelService) {
        System.out.println("\n[Channel] 1:Create 2:View 3:ListByUser 4:Delete 0:Back");
        System.out.print("> ");
        switch (sc.nextLine().trim()) {
            case "1" -> createChannel(sc, channelService);
            case "2" -> viewChannel(sc, channelService);
            case "3" -> listChannels(sc, channelService);
            case "4" -> deleteChannel(sc, channelService);
            case "0" -> {
            }
            default -> System.out.println("❗ Invalid choice");
        }
    }

    private static void createChannel(Scanner sc, ChannelService channelService) {
        System.out.print("Enter name and description separated by space: ");
        String[] parts = sc.nextLine().split("\\s+", 2);
        if (parts.length < 2) {
            System.out.println("❗ Provide both name and description");
            return;
        }
        var channel = channelService.create(
                new PublicChannelCreateRequest(parts[0], parts[1]));
        System.out.println("✅ Created: " + channel);
    }

    private static void viewChannel(Scanner sc, ChannelService channelService) {
        System.out.print("Enter channelId: ");
        try {
            UUID id = UUID.fromString(sc.nextLine());
            ChannelDto dto = channelService.find(id);
            System.out.println(dto);
        } catch (IllegalArgumentException e) {
            System.out.println("❗ Invalid UUID format");
        }
    }

    private static void listChannels(Scanner sc, ChannelService channelService) {
        System.out.print("Enter userId to list channels: ");
        try {
            UUID userId = UUID.fromString(sc.nextLine());
            List<ChannelDto> channels = channelService.findAllByUserId(userId);
            if (channels.isEmpty()) System.out.println("No channels found for user.");
            else channels.forEach(System.out::println);
        } catch (IllegalArgumentException e) {
            System.out.println("❗ Invalid UUID format");
        }
    }

    private static void deleteChannel(Scanner sc, ChannelService channelService) {
        System.out.print("Enter channelId to delete: ");
        try {
            UUID id = UUID.fromString(sc.nextLine());
            channelService.delete(id);
            System.out.println("🗑️ Deleted");
        } catch (IllegalArgumentException e) {
            System.out.println("❗ Invalid UUID format");
        }
    }

    private static void messageMenu(Scanner sc, MessageService messageService) {
        System.out.println("\n[Message] 1:Create 2:View 3:ListInChannel 4:Delete 0:Back");
        System.out.print("> ");
        switch (sc.nextLine().trim()) {
            case "1" -> createMessage(sc, messageService);
            case "2" -> viewMessage(sc, messageService);
            case "3" -> listMessages(sc, messageService);
            case "4" -> deleteMessage(sc, messageService);
            case "0" -> {
            }
            default -> System.out.println("❗ Invalid choice");
        }
    }

    private static void createMessage(Scanner sc, MessageService messageService) {
        System.out.print("Enter channelId, authorId, content: ");
        String[] parts = sc.nextLine().split("\\s+", 3);
        if (parts.length < 3) {
            System.out.println("❗ Provide channelId, authorId, content");
            return;
        }
        try {
            var msg = messageService.create(
                    new MessageCreateRequest(parts[2], UUID.fromString(parts[0]), UUID.fromString(parts[1])),
                    List.of());
            System.out.println("✅ Created: " + msg);
        } catch (IllegalArgumentException e) {
            System.out.println("❗ Invalid UUID format");
        }
    }

    private static void viewMessage(Scanner sc, MessageService messageService) {
        System.out.print("Enter messageId: ");
        try {
            UUID id = UUID.fromString(sc.nextLine());
            System.out.println(messageService.find(id));
        } catch (IllegalArgumentException e) {
            System.out.println("❗ Invalid UUID format");
        }
    }

    private static void listMessages(Scanner sc, MessageService messageService) {
        System.out.print("Enter channelId to list: ");
        try {
            UUID id = UUID.fromString(sc.nextLine());
            messageService.findAllByChannelId(id).forEach(System.out::println);
        } catch (IllegalArgumentException e) {
            System.out.println("❗ Invalid UUID format");
        }
    }

    private static void deleteMessage(Scanner sc, MessageService messageService) {
        System.out.print("Enter messageId to delete: ");
        try {
            UUID id = UUID.fromString(sc.nextLine());
            messageService.delete(id);
            System.out.println("🗑️ Deleted");
        } catch (IllegalArgumentException e) {
            System.out.println("❗ Invalid UUID format");
        }
    }
}



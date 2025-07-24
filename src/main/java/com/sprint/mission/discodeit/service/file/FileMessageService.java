package com.sprint.mission.discodeit.service.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.FileService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class FileMessageService implements MessageService, FileService {
	private static final String DATA_DIR = "data/";
	private static final String MESSAGES_FILE = DATA_DIR + "messages";

	private final Map<UUID, Message> messageMap;
	// 참조
	private final UserService userService;
	private final ChannelService channelService;

	public FileMessageService(UserService userService, ChannelService channelService) {
		this.userService = userService;
		this.channelService = channelService;

		messageMap = new ConcurrentHashMap<>();

		createDirectoryIfNotExists();
		loadFile(MESSAGES_FILE, messageMap);
	}

	@Override
	public boolean createMessage(UUID authorUUID, UUID channelUUID, String text) {
		if(authorUUID == null || channelUUID == null || text == null) return false;

		User user = userService.getUserById(authorUUID);
		Channel channel = channelService.getChannelByUUID(channelUUID);
		if(user == null || channel == null) return false;

		if(!channel.getChannelUsersUUID().contains(authorUUID)) {
			return false;
		}

		Message message = new Message(authorUUID,channelUUID, text);

		messageMap.put(message.getId(), message);
		channel.addMessage(message.getId());

		saveFile(MESSAGES_FILE, messageMap);
		if (channelService instanceof FileChannelService) {
			((FileChannelService) channelService).saveChannelData();
		}

		return true;
	}

	@Override
	public Message getMessage(UUID messageUUID) {
		return messageMap.get(messageUUID);
	}

	@Override
	public List<Message> getAllMessages() {
		return new ArrayList<Message>(messageMap.values());
	}

	@Override
	public List<Message> getMessageByAuthor(String targetAuthor, UUID channelUUID) {
		Channel channel = channelService.getChannelByUUID(channelUUID);
		if(channel == null) return null;

		List <Message> messageList = new ArrayList<>();

		// nickName으로 User UUID 구하기
		UUID targetUserUUID = null;
		for (Map.Entry<UUID, String> entry : channel.getUserNicknames().entrySet()) {
			if (targetAuthor.equals(entry.getValue())) {
				targetUserUUID = entry.getKey();
				break;
			}
		}
		if(targetUserUUID == null) return new ArrayList<>();

		// UUID로 비교해서 메시지가 있으면 list에 추가
		for (Message message : messageMap.values()) {
			if (message.getChannelUUID().equals(channelUUID) &&
				message.getAuthorUUID().equals(targetUserUUID)) {
				messageList.add(message);
			}
		}

		return messageList;
	}

	@Override
	public List<Message> getMessageByChannel(UUID channelUUID) {
		List <Message> messageList = new ArrayList<>();

		for (Message message : messageMap.values()) {
			if (message.getChannelUUID().equals(channelUUID)) {
				messageList.add(message);
			}
		}

		return messageList;
	}

	@Override
	public boolean updateMessage(UUID messageUUID, UUID authorUUID, String text) {
		if (messageUUID == null || authorUUID == null || text == null) return false;

		Message message = messageMap.get(messageUUID);
		if (!message.getAuthorUUID().equals(authorUUID)) return false;

		message.updateText(text);
		message.updateUpdatedAt();

		saveFile(MESSAGES_FILE, messageMap);

		return true;
	}



	@Override
	public void deleteMessage(UUID messageUUID, UUID authorUUID) {
		if (messageUUID == null || authorUUID == null) return;

		Message message = messageMap.get(messageUUID);
		if (message == null || !message.getAuthorUUID().equals(authorUUID)) return;

		messageMap.remove(messageUUID);

		Channel channel = channelService.getChannelByUUID(message.getChannelUUID());
		if (channel != null) {
			channel.removeMessage(messageUUID);
		}

		saveFile(MESSAGES_FILE, messageMap);

	}

	@Override
	public void createDirectoryIfNotExists() {
		try {
			Path dataPath = Paths.get(DATA_DIR);
			if (!Files.exists(dataPath)) {
				Files.createDirectories(dataPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadFile(String filename, Map map) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
			Map load = (Map) ois.readObject();
			map.putAll(load);
		} catch (FileNotFoundException ignored) {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveFile(String filename, Object data) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
			oos.writeObject(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

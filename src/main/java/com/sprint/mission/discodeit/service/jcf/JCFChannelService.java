package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

public class JCFChannelService implements ChannelService {
	private final Map<UUID, Channel> channelMap;
	// channelName 으로 UUID를 맵핑 ( Cache )
	private final Map<String, UUID> channelNameToUUID;

	public JCFChannelService() {
		channelMap = new ConcurrentHashMap<>();
		channelNameToUUID = new ConcurrentHashMap<>();
	}

	@Override
	public Channel createChannel(String channelName) {
		if(channelNameToUUID.containsKey(channelName)) return null;
		Channel channel = new Channel(channelName);

		channelMap.put(channel.getId(), channel);
		channelNameToUUID.put(channelName, channel.getId());

		return channel;
	}

	@Override
	public boolean joinChannel(User user, String channelName) {
		if(!channelNameToUUID.containsKey(channelName)) return false;

		Channel channel = channelMap.get(channelNameToUUID.get(channelName));
		channel.addUser(user.getId());
		channel.addNickname(user.getId(), user.getDefaultNickname());
		return true;
	}

	@Override
	public Channel getChannelByName(String channelName) {
		if(!channelNameToUUID.containsKey(channelName)) return null;
		return channelMap.get(channelNameToUUID.get(channelName));
	}

	@Override
	public Channel getChannelByUUID(UUID channelUUID) {
		return channelMap.get(channelUUID);
	}

	@Override
	public List<String> getMemberNicknames(String channelName){
		if (channelName == null || !channelNameToUUID.containsKey(channelName)) return new ArrayList<>();

		Channel channel = channelMap.get(channelNameToUUID.get(channelName));
		if (channel == null) return new ArrayList<>();

		List<String> nicknameList = new ArrayList<>(channel.getUserNicknames().values());
		nicknameList.sort((n1, n2) -> n1.compareTo(n2));

		return nicknameList;
	}

	@Override
	public List<Channel> getAllChannels() {
		List<Channel> channelList = new ArrayList<>(channelMap.values());
		channelList.sort((c1, c2) -> c1.getChannelName().compareTo(c2.getChannelName()));
		return channelList;
	}

	@Override
	public boolean updateChannelName(User user, UUID channelUUID, String channelNewName) {
		// 새로운 이름이 이미 사용 중이거나 업데이트 할 채널이 없을 경우 return false
		if(channelNameToUUID.containsKey(channelNewName) ||
			!channelMap.containsKey(channelUUID)){
			return false;
		}

		Channel channel = channelMap.get(channelUUID);
		channel.updateChannelName(channelNewName);
		channel.updateUpdatedAt();

		deleteChannel(channelUUID);

		channelMap.put(channelUUID, channel);
		channelNameToUUID.put(channelNewName, channel.getId());

		return true;
	}

	@Override
	public boolean updateUserNickname(UUID channelUUID, UUID userUUID, String newNickname) {
		if (newNickname == null || newNickname.isEmpty()) return false;

		Channel channel = getChannelByUUID(channelUUID);
		if (channel == null) return false;

		channel.addNickname(userUUID, newNickname);
		channel.updateUpdatedAt();
		return true;
	}

	@Override
	public boolean leaveChannel(UUID channelUUID, UUID userUUID) {
		if(!channelMap.containsKey(channelUUID)) return false;
		channelMap.get(channelUUID).removeUser(userUUID);
		channelMap.get(channelUUID).removeNickname(userUUID);

		return true;
	}

	@Override
	public boolean deleteChannel(UUID channelUUID) {
		if(!channelMap.containsKey(channelUUID)) return false;

		String channelName = channelMap.get(channelUUID).getChannelName();

		channelMap.remove(channelUUID);
		channelNameToUUID.remove(channelName);

		return true;
	}

	@Override
	public boolean deleteChannel(String channelName) {
		if(!channelNameToUUID.containsKey(channelName)) return false;

		UUID channelUUID = channelNameToUUID.get(channelName);

		channelMap.remove(channelUUID);
		channelNameToUUID.remove(channelName);

		return true;
	}

}

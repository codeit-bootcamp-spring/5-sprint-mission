package com.sprint.mission.discodeit.entity;

public class Message extends Base{

    private final User sender; //발신자는 변경할 수 없음
    private Channel channel;
    private String content;

    public Message(User sender, Channel channel, String content) {
        this.sender = sender;
        this.channel = channel;
        this.content = content;
    }

    public Channel getChannel() {return channel;}

    public void updateChannel(Channel newChannel) {
        this.channel = newChannel;
        updateTimestamp();
    }

    public User getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
        updateTimestamp();
    }


    @Override
    public String toString() {
        String sendTime = getCreatedAtFormatted();
        String senderName = this.sender.getName();
        String message = getContent();
        return "\n" + sendTime + "   " + senderName + ": " + message + "\n";
    }

}

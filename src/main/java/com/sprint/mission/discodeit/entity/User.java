package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.userEntity.Item;
import com.sprint.mission.discodeit.enums.userEntity.NitroPlan;
import com.sprint.mission.discodeit.enums.userEntity.Status;

import java.time.LocalDate;
import java.util.*;

public class User {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String email;
    private String nickname;
    private String username;
    private String password;
    private LocalDate birthDate;
    private boolean isSubscribedToNewsletter;
    private String phoneNumber;
    private List<User> friends;
    private List<Server> servers;
    private List<ChatRoom> chatRooms;
    private NitroPlan nitroPlan;
    private List<Item> items;
    private Status status;
    private String avatarUrl;
    private String bio;
    private boolean isVerified;
    private boolean isDeactivated;
    private boolean isBanned;

    public User(String email, String username, String password, LocalDate birthDate, boolean isSubscribedToNewsletter, String nickname) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;

        this.email = email;
        this.username = username;
        this.password = password;
        this.birthDate = birthDate;
        this.isSubscribedToNewsletter = isSubscribedToNewsletter;
        this.nickname = nickname;

        this.friends = new ArrayList<>();
        this.servers = new ArrayList<>();
        this.chatRooms = new ArrayList<>();
        this.nitroPlan = NitroPlan.NONE;
        this.items = new ArrayList<>();
        this.status = Status.OFFLINE;
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isSubscribedToNewsletter() {
        return isSubscribedToNewsletter;
    }

    public void setSubscribedToNewsletter(boolean subscribedToNewsletter) {
        isSubscribedToNewsletter = subscribedToNewsletter;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public NitroPlan getNitroPlan() {
        return nitroPlan;
    }

    public void setNitroPlan(NitroPlan nitroPlan) {
        this.nitroPlan = nitroPlan;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public boolean getDeactivated() {
        return isDeactivated;
    }

    public void setDeactivated(Boolean deactivated) {
        isDeactivated = deactivated;
    }

    public boolean getBanned() {
        return isBanned;
    }

    public void setBanned(Boolean banned) {
        isBanned = banned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", birthDate=" + birthDate +
                ", isSubscribedToNewsletter=" + isSubscribedToNewsletter +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", friends=" + friends +
                ", servers=" + servers +
                ", dmRooms=" + chatRooms +
                ", nitroPlan=" + nitroPlan +
                ", items=" + items +
                ", status=" + status +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", isVerified=" + isVerified +
                ", isDeactivated=" + isDeactivated +
                ", isBanned=" + isBanned +
                '}';
    }
}

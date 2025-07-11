package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.Item;
import com.sprint.mission.discodeit.enums.NitroPlan;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class User {
    private final UUID id;
    private long createdAt;
    private long updatedAt;
    private String email;
    private String nickname;
    private String username;
    private String password;
    private LocalDate birthDate;
    private boolean isSubscribedToNewsletter;
    private String phoneNumber;
    private List<User> friends;
    private Server[] servers;
    private List<List<Message>> dmRooms;
    private NitroPlan nitroPlan;
    private List<Item> items;

    public User(String email, String nickname, String username, String password, LocalDate birthDate,
                boolean isSubscribedToNewsletter, String phoneNumber, List<User> friends, Server[] servers,
                List<List<Message>> dmRooms, NitroPlan nitroPlan, List<Item> items) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.email = email;
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.birthDate = birthDate;
        this.isSubscribedToNewsletter = isSubscribedToNewsletter;
        this.phoneNumber = phoneNumber;
        this.friends = friends;
        this.servers = servers;
        this.dmRooms = dmRooms;
        this.nitroPlan = nitroPlan;
        this.items = items;
    }

    public User(String email, String nickname, String username, String password, LocalDate birthDate,
                boolean isSubscribedToNewsletter) {
        this(email, nickname, username, password, birthDate, isSubscribedToNewsletter, null, null,
                null, null, null, null);
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
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

    public Server[] getServers() {
        return servers;
    }

    public void setServers(Server[] servers) {
        this.servers = servers;
    }

    public List<List<Message>> getDmRooms() {
        return dmRooms;
    }

    public void setDmRooms(List<List<Message>> dmRooms) {
        this.dmRooms = dmRooms;
    }

    @Override
    public String toString() {
        return "User{" + "email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", birthDate=" + birthDate +
                ", isSubscribedToNewsletter=" + isSubscribedToNewsletter +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", friends=" + friends +
                ", servers=" + Arrays.toString(servers) +
                ", dmRooms=" + dmRooms +
                ", nitroPlan=" + nitroPlan +
                ", items=" + items +
                '}';
    }
}

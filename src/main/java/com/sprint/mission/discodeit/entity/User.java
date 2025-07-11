package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.Item;
import com.sprint.mission.discodeit.enums.NitroPlan;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class User {
    private final UUID id;
    private long createdAt;
    private long updatedAt;
    private String email;
    private String password;
    private String phoneNumber;
    private String username;
    private String nickname;
    private LocalDate birthDay;
    private boolean isSubscribedToNewsletter;
    private List<User> friends;
    private NitroPlan nitroPlan;
    private List<Item> items;
    private Server[] servers;
    private List<List<Message>> dmRooms;

    public User(String email, String password, String phoneNumber, String username, String nickname, LocalDate birthDay,
                boolean isSubscribedToNewsletter, List<User> friends, NitroPlan nitroPlan, List<Item> items, Server[] servers,
                List<List<Message>> dmRooms) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.nickname = nickname;
        this.birthDay = birthDay;
        this.isSubscribedToNewsletter = isSubscribedToNewsletter;
        this.friends = friends;
        this.nitroPlan = nitroPlan;
        this.items = items;
        this.servers = servers;
        this.dmRooms = dmRooms;
    }

    public User(String email, String password, String username, LocalDate birthDay) {
        this(email, password, null, username, username, birthDay, false, null, NitroPlan.NONE, null, null, null);
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

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
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
}

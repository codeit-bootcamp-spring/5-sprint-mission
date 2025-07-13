package com.sprint.mission.discodeit.entity;

import java.util.UUID;
public class user {

    private UUID id;

    public user(UUID id){
        this.id = id;
    }

    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append("}");
        return sb.toString();
    }
    public static void main(String[] args){
        System.out.println("Hello DongMin!");
    }
}

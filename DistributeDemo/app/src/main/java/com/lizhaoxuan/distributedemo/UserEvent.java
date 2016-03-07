package com.lizhaoxuan.distributedemo;

/**
 * Created by lizhaoxuan on 16/3/7.
 */
public class UserEvent {

    private int id;

    private String name;

    public UserEvent(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

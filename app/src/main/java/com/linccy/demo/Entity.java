package com.linccy.demo;

import java.io.Serializable;

/**
 * Created by btc_eth on 2018/8/27.
 */

public class Entity implements Serializable{
    private String id;
    private String name;

    public Entity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

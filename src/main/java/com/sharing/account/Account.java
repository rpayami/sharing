package com.sharing.account;

import org.springframework.data.annotation.Id;

public class Account {
    private @Id Long id;
    private String name;
    private String password;

    public Account(Long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
}

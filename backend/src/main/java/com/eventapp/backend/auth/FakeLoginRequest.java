package com.eventapp.backend.auth;

public class FakeLoginRequest {

    private String email;
    private String name;

    public FakeLoginRequest() {
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }
}
package com.W3yneRagsac.SnapShop.model.Entity;

import lombok.Data;

import java.util.List;

@Data
public class AuthPayloadEntity {
    private String token;
    private String message;
    private List<String> roles;

    public AuthPayloadEntity(String token, String message, List<String> roles) {
        this.token = token;
        this.message = message;
        this.roles = roles;
    }
}

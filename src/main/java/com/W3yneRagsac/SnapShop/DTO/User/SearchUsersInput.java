package com.W3yneRagsac.SnapShop.DTO.User;

import com.W3yneRagsac.SnapShop.model.Entity.RoleEntity;
import lombok.Data;

import java.util.Set;

@Data
public class SearchUsersInput {
    private String username;
    private  String email;
    private  Boolean isPresent;
    private String userTimeZone;
    private Set<RoleEntity> roles;
}

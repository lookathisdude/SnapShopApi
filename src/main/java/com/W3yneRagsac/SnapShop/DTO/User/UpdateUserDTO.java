package com.W3yneRagsac.SnapShop.DTO.User;

import lombok.Data;

@Data
public class UpdateUserDTO {
    // method for updating user
    private Long id;
    private String name;
    private String email;
    private String password;
}

package com.W3yneRagsac.SnapShop.DTO.User;

import lombok.Data;

@Data
public class UpdatePasswordInput {
    private String updatedPassword;
    private String confirmPassword;
}

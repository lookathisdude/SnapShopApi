package com.W3yneRagsac.SnapShop.DTO.User;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserCredentialsInput {
    private Long id;
    private String user;
    private String email;
    private String updatedPassword;
    private String confirmPassword;
}

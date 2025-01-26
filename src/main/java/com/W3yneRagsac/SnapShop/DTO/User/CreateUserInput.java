package com.W3yneRagsac.SnapShop.DTO.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserInput {
    @NotNull(message = "Name cannot be null")
    @Size(min = 1, message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Password cannot be null")
    @Size(min = 6, message = "Password should be at least 6 characters")
    private String password;

    @NotNull(message = "Confirm Password cannot be null")
    @Size(min = 6, message = "Confirm Password should be at least 6 characters")
    private String confirmPassword;

    private String userTimeZone;
}

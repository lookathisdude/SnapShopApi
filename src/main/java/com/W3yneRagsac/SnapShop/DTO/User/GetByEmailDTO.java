package com.W3yneRagsac.SnapShop.DTO.User;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetByEmailDTO {
    @NotNull(message = "Email cannot be null")
    String email;
}

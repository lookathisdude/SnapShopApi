package com.W3yneRagsac.SnapShop.DTO.User;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetByNameDTO {
    @NotNull(message = "Username cannot be null")
    String name;
}

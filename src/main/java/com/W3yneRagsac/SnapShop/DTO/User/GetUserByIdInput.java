package com.W3yneRagsac.SnapShop.DTO.User;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetUserByIdInput {
    @NotNull(message = "The id cannot be null")
    private Long id;
}

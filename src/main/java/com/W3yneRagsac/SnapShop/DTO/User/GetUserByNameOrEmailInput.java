package com.W3yneRagsac.SnapShop.DTO.User;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetUserByNameOrEmailInput {
    String name;
    String email;
}

package com.W3yneRagsac.SnapShop.model;

import com.W3yneRagsac.SnapShop.model.enums.Roles;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Roles role;
}

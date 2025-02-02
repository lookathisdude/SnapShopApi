package com.W3yneRagsac.SnapShop.model.Entity;

import com.W3yneRagsac.SnapShop.model.enums.Roles;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Roles role;

    public RoleEntity(Roles roles) {
        this.role = roles;
    }
}

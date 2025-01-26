package com.W3yneRagsac.SnapShop.repository;

import com.W3yneRagsac.SnapShop.model.RoleEntity;
import com.W3yneRagsac.SnapShop.model.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    // Find RoleEntity by role enum
    RoleEntity findByRole(Roles role);
}

package com.W3yneRagsac.SnapShop.repository;

import com.W3yneRagsac.SnapShop.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional <UserEntity> findUserByName(String name);
    Optional<UserEntity> findByEmail(String email);
}

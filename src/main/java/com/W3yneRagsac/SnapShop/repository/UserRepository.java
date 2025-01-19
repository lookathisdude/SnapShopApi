package com.W3yneRagsac.SnapShop.repository;

import com.W3yneRagsac.SnapShop.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findUserByName(String name);
    UserEntity findByEmail(String email);
}

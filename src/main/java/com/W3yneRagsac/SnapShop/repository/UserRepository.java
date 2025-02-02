package com.W3yneRagsac.SnapShop.repository;

import com.W3yneRagsac.SnapShop.model.Entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findUserByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    UserEntity findByUsername(String username);
}

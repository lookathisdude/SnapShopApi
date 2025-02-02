package com.W3yneRagsac.SnapShop.DTO.Specifications;

import com.W3yneRagsac.SnapShop.model.Entity.RoleEntity;
import com.W3yneRagsac.SnapShop.model.Entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public class UserSpecifications {

    // Specification for filtering users by username
    public static Specification<UserEntity> hasUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            // If a username is provided, filter users whose username contains the provided string
            if (username != null) {
                return criteriaBuilder.like(root.get("username"), "%" + username + "%");
            }
            // If no username is provided, return a condition that always evaluates to true (no filtering)
            return criteriaBuilder.conjunction();
        };
    }

    // Specification for filtering users by email
    public static Specification<UserEntity> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            // If an email is provided, filter users whose email contains the provided string (case-insensitive)
            if (email != null) {
                return criteriaBuilder.like(root.get("email"), "%" + email + "%");
            }
            // If no email is provided, return a condition that always evaluates to true (no filtering)
            return criteriaBuilder.conjunction();
        };
    }

    // Specification for filtering users by roles
    public static Specification<UserEntity> hasRoles(Set<RoleEntity> roles) {
        return (root, query, criteriaBuilder) -> {
            // If roles are provided, filter users that have any of the specified roles
            if (roles != null && !roles.isEmpty()) {
                return root.join("roles").in(roles);  // Joins the 'roles' relation and checks for matching roles
            }
            // If no roles are provided, return a condition that always evaluates to true (no filtering)
            return criteriaBuilder.conjunction();
        };
    }
}

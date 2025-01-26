package com.W3yneRagsac.SnapShop.service.classes;

import com.W3yneRagsac.SnapShop.model.enums.Roles;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    public Roles assignRole(Authentication authentication) {
        if(authentication == null || !authentication.isAuthenticated()) {
            return Roles.GUEST; // return the role guest if not authenticated
        }
        return Roles.CUSTOMER; // if authenticated, return the role customer
    }
}

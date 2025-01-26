package com.W3yneRagsac.SnapShop.Controller.REST;


import com.W3yneRagsac.SnapShop.model.enums.Roles;
import com.W3yneRagsac.SnapShop.service.classes.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/role")
    public String getRole(Authentication authentication) {
        Roles role = roleService.assignRole(authentication);
        return "Assigned Role: " + role.name();
    }
}

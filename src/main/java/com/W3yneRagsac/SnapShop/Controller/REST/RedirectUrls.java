package com.W3yneRagsac.SnapShop.Controller.REST;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/redirect")
public class RedirectUrls {
    // Handle successful login redirect
    @GetMapping("/success")
    public String redirectAfterLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check for the user's role (e.g., admin)
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return "redirect:/admin/dashboard";  // Redirect to admin dashboard
        } else {
            return "redirect:/customer/home";  // Redirect to customer home page (home.html)
        }
    }

    // Login page
    @GetMapping("/snapshoplogin")
    public String showLoginPage() {
        return "snapshoplogin";  // Return the login page view (snapshoplogin.html or login.jsp)
    }
}

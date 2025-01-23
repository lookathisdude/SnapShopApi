package com.W3yneRagsac.SnapShop.Controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/verify-identity")
public class VerifyMicrosoftIdentityController {

    @GetMapping("/microsoft-identity-association.json")
    public ResponseEntity<?> getMicrosoftIdentityFile() throws IOException {
        // Serve the file from the resources
        ClassPathResource resource = new ClassPathResource("static/microsoft-identity-association.json");
        return ResponseEntity.ok().body(resource);
    }
}

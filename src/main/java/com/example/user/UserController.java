package com.example.user;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public UserProfileResponse getMyProfile(Authentication authentication) {
        // TODO: 실제 구현
        String userId = authentication.getName();
        return new UserProfileResponse();
    }
}

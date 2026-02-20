package com.practiceproject.itmopracticeproject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserByLogin(userDetails.getUsername());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long userId) {
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {}
        // TODO: Сделать тут логику что может только админ запрашивать
        // TODO: Сделать в бдшке login unique
        return null;
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateUserRequest request) {
        return userService.updateUser(userDetails.getUsername(), request);
    }

    //TODO: Добавить поиск, и изменение юзера админом
}

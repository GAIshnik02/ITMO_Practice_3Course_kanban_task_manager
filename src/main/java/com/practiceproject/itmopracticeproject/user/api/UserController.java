package com.practiceproject.itmopracticeproject.user.api;

import com.practiceproject.itmopracticeproject.security.CurrentUser;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import com.practiceproject.itmopracticeproject.user.dto.UserChangePasswordRequest;
import com.practiceproject.itmopracticeproject.user.dto.UserResponseDto;
import com.practiceproject.itmopracticeproject.user.dto.UserUpdateRequestDto;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getUserById(
            @CurrentUser UserEntity currentUser) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(currentUser.getId()));
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUserById(
            @CurrentUser UserEntity currentUser
    ) {
        userService.deleteUserById(currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUserById(
            @Valid @RequestBody UserUpdateRequestDto request,
            @CurrentUser UserEntity currentUser
    ) {
        UserResponseDto updated = userService.updateUser(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updated);
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody UserChangePasswordRequest request,
            @CurrentUser UserEntity currentUser
    ) {
        userService.changePassword(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}

package com.practiceproject.itmopracticeproject.user.api;

import com.practiceproject.itmopracticeproject.security.CurrentUser;
import com.practiceproject.itmopracticeproject.user.db.GlobalRole;
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
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    //TODO: Добавить поиск всех пользователей, фильтр для бдшки (пагинацию)

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable("id") Long userId,
            @CurrentUser UserEntity currentUser) {
        if (currentUser.getRole() != GlobalRole.ADMIN) {
            throw new SecurityException("Access denied");
        }

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(
            @PathVariable("id") Long userId,
            @CurrentUser UserEntity currentUser
    ) {
        if (currentUser.getRole() != GlobalRole.ADMIN) {
            throw new SecurityException("Access denied");
        }
        userService.deleteUserById(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById(
            @PathVariable("id")  Long userId,
            @Valid @RequestBody UserUpdateRequestDto request,
            @CurrentUser UserEntity currentUser
    ) {
        if (currentUser.getRole() != GlobalRole.ADMIN) {
            throw new SecurityException("Access denied");
        }
        UserResponseDto updated = userService.updateUser(userId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updated);
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable("id") Long userId,
            @RequestBody UserChangePasswordRequest request,
            @CurrentUser UserEntity currentUser
    ) {
        if (currentUser.getRole() != GlobalRole.ADMIN) {
            throw new SecurityException("Access denied");
        }
        userService.changePassword(userId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}

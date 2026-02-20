package com.practiceproject.itmopracticeproject;

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

    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser (
           @RequestBody @Valid UserDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable("id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body( userService.getUserById(userId));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteUserById(
            @PathVariable("id") Long userId
    ) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<UserDto> updateUserById(
            @PathVariable("id")  Long userId,
            @RequestBody @Valid UserDto request
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.updateUser(userId, request));
    }



    //TODO: Добавить поиск, и изменение юзера админом
}

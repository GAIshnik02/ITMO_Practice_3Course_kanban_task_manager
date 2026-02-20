package com.practiceproject.itmopracticeproject;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public ResponseEntity<UserResponse> getUserByLogin(String username) {
    }

    public ResponseEntity<UserResponse> updateUser(String username, UpdateUserRequest request) {
    }
}

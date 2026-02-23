package com.practiceproject.itmopracticeproject.auth.dto;

public record AuthResponse(
        String token,
        String login,
        Long userId
) {
}

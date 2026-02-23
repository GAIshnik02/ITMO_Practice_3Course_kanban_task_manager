package com.practiceproject.itmopracticeproject.auth.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull String login,
        @NotNull String password
) {
}

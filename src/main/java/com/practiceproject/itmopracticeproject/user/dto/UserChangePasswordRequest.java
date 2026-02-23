package com.practiceproject.itmopracticeproject.user.dto;

import jakarta.validation.constraints.NotNull;

public record UserChangePasswordRequest(
        @NotNull String oldPassword,
        @NotNull String newPassword
) {
}

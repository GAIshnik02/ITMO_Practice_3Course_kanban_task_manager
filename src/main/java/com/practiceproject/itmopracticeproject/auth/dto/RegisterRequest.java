package com.practiceproject.itmopracticeproject.auth.dto;

import jakarta.validation.constraints.NotNull;

public record RegisterRequest (
       @NotNull String login,
       @NotNull String password,
       String first_Name,
       String surname,
       String patronymic
) {}

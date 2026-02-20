package com.practiceproject.itmopracticeproject;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDateTime;

public record UserDto(
    @Null
    Long id,
    @NotNull
    String login,
    @NotNull
    String pass_hash,
    String first_name,
    String surname,
    String patronymic,
    @Null
    LocalDateTime created_at,
    @Null
    LocalDateTime updated_at
) {

}

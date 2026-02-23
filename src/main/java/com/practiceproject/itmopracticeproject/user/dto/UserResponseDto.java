package com.practiceproject.itmopracticeproject.user.dto;


import java.time.LocalDateTime;

public record UserResponseDto(
    Long id,
    String login,
    String first_name,
    String surname,
    String patronymic,
    LocalDateTime created_at,
    LocalDateTime updated_at
) {

}

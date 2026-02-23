package com.practiceproject.itmopracticeproject.user.dto;


import com.practiceproject.itmopracticeproject.user.db.GlobalRole;

import java.time.LocalDateTime;

public record UserResponseDto(
    Long id,
    String login,
    String first_name,
    String surname,
    String patronymic,
    GlobalRole role,
    LocalDateTime created_at,
    LocalDateTime updated_at
) {

}

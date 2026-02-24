package com.practiceproject.itmopracticeproject.user.dto;

public record UserUpdateRequestDto(
        String first_name,
        String surname,
        String patronymic
) {}
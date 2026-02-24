package com.practiceproject.itmopracticeproject.boards.dto;

import jakarta.validation.constraints.NotBlank;

public record BoardRequestDto(
     @NotBlank
     String name,

     String description
) {}

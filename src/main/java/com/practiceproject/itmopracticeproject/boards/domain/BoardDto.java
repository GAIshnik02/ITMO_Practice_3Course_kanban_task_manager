package com.practiceproject.itmopracticeproject.boards.domain;

import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDateTime;

public record BoardDto(
     @Null
     Long id,
     @NotNull
     String name,

     String description,

     @NotNull
     Long owner_id,

     LocalDateTime created_at,

     LocalDateTime updated_at
) {}

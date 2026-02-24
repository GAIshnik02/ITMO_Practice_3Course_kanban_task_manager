package com.practiceproject.itmopracticeproject.boards.dto;

import java.time.LocalDateTime;

public record BoardResponseDto(
        Long id,
        String name,
        String description,
        //TODO: Реализовать смену владельца доски
        Long owner_id,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {}

package com.practiceproject.itmopracticeproject.task.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TaskResponseDto(
        Long id,
        Long boardId,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Integer position,
        Long creatorId,
        List<Long> assigneeIds,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {}
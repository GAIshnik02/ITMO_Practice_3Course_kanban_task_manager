package com.practiceproject.itmopracticeproject.task.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;


public record TaskCreateRequestDto(
        @NotNull
        String title,

        String description,

        @NotNull
        TaskStatus status,

        @NotNull
        TaskPriority priority,

        @NotNull
        Integer position,

        @NotNull
        Long creatorId,

        List<Long> assigneeIds
) {
}

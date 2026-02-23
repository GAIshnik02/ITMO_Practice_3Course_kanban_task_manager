package com.practiceproject.itmopracticeproject.task.domain;

import com.practiceproject.itmopracticeproject.task.TaskPriority;
import com.practiceproject.itmopracticeproject.task.TaskStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record TaskUpdateRequestDto(
        @NotNull
        String title,

        String description,

        @NotNull
        TaskStatus status,

        @NotNull
        TaskPriority priority,

        @NotNull
        Integer position,

        List<Long> assigneeIds
) {
}

package com.practiceproject.itmopracticeproject.board_members.dto;

import jakarta.validation.constraints.NotNull;

public record CreateBoardMemberRequest(
        @NotNull Long userId,
        @NotNull Role role
) {}

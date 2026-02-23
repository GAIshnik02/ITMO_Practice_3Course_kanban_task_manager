package com.practiceproject.itmopracticeproject.board_members.api;

import com.practiceproject.itmopracticeproject.board_members.domain.Role;
import jakarta.validation.constraints.NotNull;

public record CreateBoardMemberRequest(
        @NotNull Long userId,
        @NotNull Role role
) {}

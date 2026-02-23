package com.practiceproject.itmopracticeproject.board_members.domain;

import java.time.LocalDateTime;

public record BoardMemberDto(
        Long boardId,
        Long userId,
        Role role,
        LocalDateTime joined_at,
        LocalDateTime left_at
) {}
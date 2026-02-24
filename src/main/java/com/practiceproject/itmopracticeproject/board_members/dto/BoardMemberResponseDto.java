package com.practiceproject.itmopracticeproject.board_members.dto;

import java.time.LocalDateTime;

public record BoardMemberResponseDto(
        Long boardId,
        Long userId,
        Role role,
        LocalDateTime joined_at,
        LocalDateTime left_at
) {}
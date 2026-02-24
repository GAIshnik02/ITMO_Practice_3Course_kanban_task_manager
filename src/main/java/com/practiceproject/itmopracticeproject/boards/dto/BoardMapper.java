package com.practiceproject.itmopracticeproject.boards.dto;

import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import org.springframework.stereotype.Component;

@Component
public class BoardMapper {

    public BoardResponseDto toDto(BoardEntity boardEntity) {
        return new BoardResponseDto(
                boardEntity.getId(),
                boardEntity.getName(),
                boardEntity.getDescription(),
                boardEntity.getOwner().getId(),
                boardEntity.getCreated_at(),
                boardEntity.getUpdated_at()
        );
    }

}

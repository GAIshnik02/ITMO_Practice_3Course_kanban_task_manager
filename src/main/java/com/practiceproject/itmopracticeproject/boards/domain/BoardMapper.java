package com.practiceproject.itmopracticeproject.boards.domain;

import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class BoardMapper {

    public BoardDto toBoardDto(BoardEntity boardEntity) {
        return new BoardDto(
                boardEntity.getId(),
                boardEntity.getName(),
                boardEntity.getDescription(),
                boardEntity.getOwnerId().getId(),
                boardEntity.getCreated_at(),
                boardEntity.getUpdated_at()
        );
    }

    public BoardEntity toBoardEntity(BoardDto boardDto, UserEntity userEntity) {
        return new BoardEntity(
                boardDto.id(),
                boardDto.name(),
                boardDto.description(),
                userEntity,
                boardDto.created_at(),
                boardDto.updated_at()
        );
    }
}

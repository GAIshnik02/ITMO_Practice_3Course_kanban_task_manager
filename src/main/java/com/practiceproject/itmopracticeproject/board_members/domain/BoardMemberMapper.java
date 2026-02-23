package com.practiceproject.itmopracticeproject.board_members.domain;

import com.practiceproject.itmopracticeproject.board_members.api.CreateBoardMemberRequest;
import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberEntity;
import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberId;
import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BoardMemberMapper {

    public BoardMemberDto toDto(BoardMemberEntity entity) {
        return new BoardMemberDto(
                entity.getBoard().getId(),
                entity.getUser().getId(),
                entity.getRole(),
                entity.getJoined_at(),
                entity.getLeft_at()
        );
    }

    public BoardMemberEntity toEntity(
            BoardEntity board,
            UserEntity user,
            CreateBoardMemberRequest request
    ) {
        BoardMemberId id = new BoardMemberId(board.getId(), user.getId());

        var entity = new BoardMemberEntity();
        entity.setId(id);
        entity.setBoard(board);
        entity.setUser(user);
        entity.setRole(request.role());

        return entity;
    }

    public void updateRole(BoardMemberEntity entity, Role role) {
        entity.setRole(role);
    }

    public void markAsLeft(BoardMemberEntity entity) {
        entity.setLeft_at(LocalDateTime.now());
    }
}

package com.practiceproject.itmopracticeproject.board_members.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardMemberRepository extends JpaRepository<BoardMemberEntity, Long> {
    List<BoardMemberEntity> findAllByBoardId(Long boardId);

    Optional<BoardMemberEntity> findByBoardIdAndUserId(Long boardId, Long userId);
}

package com.practiceproject.itmopracticeproject.boards.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    @Query("SELECT DISTINCT b FROM BoardEntity b " +
            "LEFT JOIN BoardMemberEntity bm ON b.id = bm.board.id " +
            "WHERE b.owner.id = :userId OR bm.user.id = :userId")
    List<BoardEntity> findAllAccessibleBoardsByUserId(@Param("userId") Long userId);
}
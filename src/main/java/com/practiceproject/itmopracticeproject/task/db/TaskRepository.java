package com.practiceproject.itmopracticeproject.task.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query("""
        SELECT DISTINCT t
        FROM TaskEntity t
        LEFT JOIN FETCH t.assignees
        WHERE t.board.id = :boardId
        ORDER BY t.position
    """)
    List<TaskEntity> findAllByBoardIdWithAssignees(@Param("boardId") Long boardId);

    @Query("""
        SELECT DISTINCT t
        FROM TaskEntity t
        LEFT JOIN FETCH t.assignees
        WHERE t.id = :taskId
    """)
    Optional<TaskEntity> findByIdWithAssignees(@Param("taskId") Long taskId);
}
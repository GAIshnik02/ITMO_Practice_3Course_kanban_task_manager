package com.practiceproject.itmopracticeproject.task.dto;

import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.task.db.TaskEntity;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TaskMapper {
    public TaskResponseDto toDto(TaskEntity taskEntity) {
        return new TaskResponseDto(
                taskEntity.getId(),
                taskEntity.getBoard().getId(),
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getStatus(),
                taskEntity.getPriority(),
                taskEntity.getPosition(),
                taskEntity.getCreator().getId(),
                taskEntity.getAssignees().stream()
                          .map(UserEntity::getId)
                          .toList(),
                taskEntity.getCreated_at(),
                taskEntity.getUpdated_at()
        );
    }

    public TaskEntity toEntity(
            TaskCreateRequestDto dto,
            BoardEntity board,
            UserEntity creator,
            Set<UserEntity> assignees
    ) {
        TaskEntity entity = new TaskEntity();
        entity.setBoard(board);
        entity.setTitle(dto.title());
        entity.setDescription(dto.description());
        entity.setStatus(dto.status());
        entity.setPriority(dto.priority());
        entity.setPosition(dto.position());
        entity.setCreator(creator);
        entity.setAssignees(assignees);
        return entity;
    }

    public void updateEntity(
            TaskEntity entity,
            TaskUpdateRequestDto dto,
            Set<UserEntity> assignees
    ) {
        entity.setTitle(dto.title());
        entity.setDescription(dto.description());
        entity.setStatus(dto.status());
        entity.setPriority(dto.priority());
        entity.setPosition(dto.position());
        if (assignees != null) {
            entity.setAssignees(assignees);
        }
    }
}

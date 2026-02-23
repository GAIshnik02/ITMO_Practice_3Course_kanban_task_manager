package com.practiceproject.itmopracticeproject.task.api;

import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.boards.db.BoardRepository;
import com.practiceproject.itmopracticeproject.task.TaskStatus;
import com.practiceproject.itmopracticeproject.task.db.TaskEntity;
import com.practiceproject.itmopracticeproject.task.db.TaskRepository;
import com.practiceproject.itmopracticeproject.task.domain.TaskCreateRequestDto;
import com.practiceproject.itmopracticeproject.task.domain.TaskResponseDto;
import com.practiceproject.itmopracticeproject.task.domain.TaskUpdateRequestDto;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import com.practiceproject.itmopracticeproject.user.db.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final TaskMapper mapper;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository,
            BoardRepository boardRepository,
            TaskMapper mapper
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.mapper = mapper;
    }

    public List<TaskResponseDto> getAllTasks(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new EntityNotFoundException("Board with id " + boardId + " not found");
        }
        var taskEntities = taskRepository.findAllByBoardIdWithAssignees(boardId);
        return taskEntities.stream()
                           .map(mapper::toDto)
                           .toList();
    }

    public TaskResponseDto getTask(Long id) {
        TaskEntity taskEntity = taskRepository.findByIdWithAssignees(id)
                                              .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
        return mapper.toDto(taskEntity);
    }


    public TaskResponseDto createTask(
            Long boardId,
            TaskCreateRequestDto dto
    ) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Board with id " + boardId + " not found")
                );
        UserEntity creator = userRepository.findById(dto.creatorId())
                .orElseThrow(
                        () -> new EntityNotFoundException("User with id " + dto.creatorId() + " not found")
                );
        Set<UserEntity> assignees = dto.assigneeIds() != null ?
                dto.assigneeIds().stream()
                   .map(id -> userRepository.findById(id).orElseThrow(
                           () -> new EntityNotFoundException("User with id " + id + " not found")
                   )).collect(Collectors.toSet()) : Set.of();

        TaskEntity task = mapper.toEntity(dto, board, creator, assignees);

        taskRepository.save(task);

        return mapper.toDto(task);
    }

    public TaskResponseDto updateTask(Long boardId, Long taskId, TaskUpdateRequestDto dto) {
        TaskEntity task = taskRepository.findByIdWithAssignees(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id " + taskId + " not found")
        );

        if (!task.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException("Task does not belong to this board");
        }

        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setPosition(dto.position());
        task.setPriority(dto.priority());
        task.setStatus(dto.status());

        if (dto.assigneeIds() != null) {
            Set<UserEntity> assignees = dto.assigneeIds().stream()
                    .map(id -> userRepository.findById(id).orElseThrow(
                            () -> new EntityNotFoundException("User "+ id + " not found")
                    ))
                    .collect(Collectors.toSet());
            task.setAssignees(assignees);
        }

        TaskEntity updatedTask = taskRepository.save(task);
        return mapper.toDto(updatedTask);
    }

    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new EntityNotFoundException("Task with id " + taskId + " not found");
        }
        taskRepository.deleteById(taskId);
    }

    public TaskResponseDto updateTaskStatus(Long taskId, TaskStatus status) {
        TaskEntity task = taskRepository.findById(taskId)
                                        .orElseThrow(
                                                () -> new EntityNotFoundException("Task with id " + taskId + " not found")
                                        );
        task.setStatus(status);
        return mapper.toDto(taskRepository.save(task));
    }

    public TaskResponseDto updateAssignees(Long taskId, List<Long> assigneeIds) {
        TaskEntity task = taskRepository.findByIdWithAssignees(taskId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Task with id " + taskId + " not found")
                );

        Set<UserEntity> assignees = assigneeIds.stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("User " + id + " not found")
                        ))
                .collect(Collectors.toSet());

        task.setAssignees(assignees);
        return mapper.toDto(taskRepository.save(task));
    }
}
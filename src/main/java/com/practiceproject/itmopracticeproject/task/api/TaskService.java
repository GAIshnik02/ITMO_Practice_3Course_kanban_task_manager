package com.practiceproject.itmopracticeproject.task.api;

import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberEntity;
import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberRepository;
import com.practiceproject.itmopracticeproject.board_members.dto.Role;
import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.boards.db.BoardRepository;
import com.practiceproject.itmopracticeproject.task.dto.*;
import com.practiceproject.itmopracticeproject.task.db.TaskEntity;
import com.practiceproject.itmopracticeproject.task.db.TaskRepository;
import com.practiceproject.itmopracticeproject.user.db.GlobalRole;
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
    private final BoardMemberRepository boardMemberRepository;
    private final TaskMapper mapper;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository,
            BoardRepository boardRepository,
            BoardMemberRepository boardMemberRepository,
            TaskMapper mapper
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.boardMemberRepository = boardMemberRepository;
        this.mapper = mapper;
    }

    public List<TaskResponseDto> getAllTasks(Long boardId, UserEntity user) {
        if (!boardRepository.existsById(boardId)) {
            throw new EntityNotFoundException("Board with id " + boardId + " not found");
        }

        checkBoardAccess(boardId, user);

        var taskEntities = taskRepository.findAllByBoardIdWithAssignees(boardId);
        return taskEntities.stream()
                           .map(mapper::toDto)
                           .toList();
    }

    public TaskResponseDto getTask(Long boardId, Long taskId, UserEntity user) {

        checkBoardAccess(boardId, user);

        TaskEntity taskEntity = taskRepository
                .findByIdWithAssignees(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " not found"));
        return mapper.toDto(taskEntity);
    }

    public TaskResponseDto createTask(
            Long boardId,
            TaskCreateRequestDto request,
            UserEntity user
    ) {
        var creatingUserMemberEntity = checkBoardAccess(boardId, user);

        validateCreatePermissions(creatingUserMemberEntity, user);

        BoardEntity board = boardRepository
                .findById(boardId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Board with id " + boardId + " not found")
                );
        UserEntity creator = userRepository
                .findById(user.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("User with id " + user.getId() + " not found")
                );
        Set<UserEntity> assignees = request.assigneeIds() != null ?
                request.assigneeIds()
                       .stream()
                       .map(id -> userRepository.findById(id).orElseThrow(
                               () -> new EntityNotFoundException("User with id " + id + " not found")
                       )).collect(Collectors.toSet()) : Set.of();

        TaskEntity task = mapper.toEntity(request, board, creator, assignees);

        taskRepository.save(task);

        return mapper.toDto(task);
    }

    public TaskResponseDto updateTask(Long boardId, Long taskId, TaskUpdateRequestDto dto, UserEntity user) {

        var updatingUserMemberEntity = checkBoardAccess(boardId, user);

        TaskEntity task = taskRepository.findByIdWithAssignees(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id " + taskId + " not found")
        );

        if (!task.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException("Task does not belong to this board");
        }

        validateUpdatePermissions(updatingUserMemberEntity, user);

        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setPosition(dto.position());
        task.setPriority(dto.priority());
        task.setStatus(dto.status());

        if (dto.assigneeIds() != null) {
            Set<UserEntity> assignees = dto.assigneeIds()
                                           .stream()
                                           .map(id -> userRepository.findById(id).orElseThrow(
                                                   () -> new EntityNotFoundException("User " + id + " not found")
                                           ))
                                           .collect(Collectors.toSet());
            task.setAssignees(assignees);
        }

        TaskEntity updatedTask = taskRepository.save(task);
        return mapper.toDto(updatedTask);
    }

    public void deleteTask(Long boardId, Long taskId, UserEntity user) {

        var deletingUserMemberEntity = checkBoardAccess(boardId, user);

        validateDeletePermissions(deletingUserMemberEntity, user);

        if (!taskRepository.existsById(taskId)) {
            throw new EntityNotFoundException("Task with id " + taskId + " not found");
        }
        taskRepository.deleteById(taskId);
    }

    public TaskResponseDto updateTaskStatus(Long boardId, Long taskId, TaskStatus status, UserEntity user) {

        var updatingUserMemberEntity = checkBoardAccess(boardId, user);

        validateUpdatePermissions(updatingUserMemberEntity, user);

        TaskEntity task = taskRepository
                .findById(taskId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Task with id " + taskId + " not found")
                );

        if (!task.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException("Task does not belong to this board");
        }

        task.setStatus(status);
        return mapper.toDto(taskRepository.save(task));
    }

    public TaskResponseDto updateAssignees(Long boardId, Long taskId, List<Long> assigneeIds, UserEntity user) {

        var updatingUserMemberEntity = checkBoardAccess(boardId, user);

        validateUpdatePermissions(updatingUserMemberEntity, user);

        TaskEntity task = taskRepository
                .findByIdWithAssignees(taskId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Task with id " + taskId + " not found")
                );

        Set<UserEntity> assignees = assigneeIds
                .stream()
                .map(id -> userRepository.findById(id)
                                         .orElseThrow(
                                                 () -> new EntityNotFoundException("User " + id + " not found")
                                         ))
                .collect(Collectors.toSet());

        task.setAssignees(assignees);
        return mapper.toDto(taskRepository.save(task));
    }

    private BoardMemberEntity checkBoardAccess(Long boardId, UserEntity user) {
        if (user.getRole().equals(GlobalRole.ADMIN)) {
            return null; // Админ может всё, но не обязательно является участником
        }

        boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException("Board with id " + boardId + " not found")
        );

        return boardMemberRepository.findByBoardIdAndUserId(boardId, user.getId()).orElseThrow(
                () -> new SecurityException("You are not allowed to access this board!")
        );
    }

    private void validateCreatePermissions(
            BoardMemberEntity currentUserMemberEntity,
            UserEntity currentUser
    ) {
        if (currentUser.getRole().equals(GlobalRole.ADMIN)) {
            return;
        }

        if (currentUserMemberEntity == null) {
            throw new SecurityException("You are not allowed to access this board!");
        }

        Role currentRole = currentUserMemberEntity.getRole();

        if (!(currentRole.equals(Role.OWNER) || currentRole.equals(Role.MEMBER))) {
            throw new SecurityException("You cannot add tasks to this board!");
        }
    }

    private void validateUpdatePermissions(
            BoardMemberEntity currentUserMemberEntity,
            UserEntity currentUser
    ) {
        if (currentUser.getRole().equals(GlobalRole.ADMIN)) {
            return;
        }

        if (currentUserMemberEntity == null) {
            throw new SecurityException("You are not allowed to access this board!");
        }

        Role currentRole = currentUserMemberEntity.getRole();

        if (!(currentRole.equals(Role.OWNER) || currentRole.equals(Role.MEMBER))) {
            throw new SecurityException("You cannot update this task!");
        }
    }

    private void validateDeletePermissions(
            BoardMemberEntity currentUserMemberEntity,
            UserEntity currentUser
    ) {
        if (currentUser.getRole().equals(GlobalRole.ADMIN)) {
            return;
        }

        if (currentUserMemberEntity == null) {
            throw new SecurityException("You are not allowed to access this board!");
        }

        Role currentRole = currentUserMemberEntity.getRole();

        if (!(currentRole.equals(Role.OWNER) || currentRole.equals(Role.MEMBER))) {
            throw new SecurityException("You cannot delete this task!");
        }
    }
}
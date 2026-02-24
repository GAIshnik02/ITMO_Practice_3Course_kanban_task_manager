package com.practiceproject.itmopracticeproject.task.api;

import com.practiceproject.itmopracticeproject.security.CurrentUser;
import com.practiceproject.itmopracticeproject.task.dto.TaskStatus;
import com.practiceproject.itmopracticeproject.task.dto.TaskCreateRequestDto;
import com.practiceproject.itmopracticeproject.task.dto.TaskResponseDto;
import com.practiceproject.itmopracticeproject.task.dto.TaskUpdateRequestDto;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{board_id}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<?> getTasks(
            @PathVariable("board_id") Long board_id,
            @CurrentUser UserEntity user
    ){
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getAllTasks(board_id, user));
    }

    @GetMapping("/{task_id}")
    public ResponseEntity<?> getTask(
            @PathVariable("board_id") Long board_id,
            @PathVariable("task_id")  Long task_id,
            @CurrentUser UserEntity user
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getTask(board_id, task_id, user));
    }

    @PostMapping
    public ResponseEntity<?> createTask(
            @PathVariable("board_id") Long boardId,
            @RequestBody @Valid TaskCreateRequestDto dto,
            @CurrentUser UserEntity user

    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(boardId, dto, user));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable("board_id") Long boardId,
            @PathVariable("taskId") Long taskId,
            @RequestBody @Valid TaskUpdateRequestDto dto,
            @CurrentUser UserEntity user
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(taskService.updateTask(boardId, taskId, dto, user));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(
            @PathVariable("board_id") Long boardId,
            @PathVariable("taskId") Long taskId,
            @CurrentUser UserEntity user
    ) {
        taskService.deleteTask(boardId, taskId, user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable("board_id") Long boardId,
            @PathVariable("taskId") Long taskId,
            @RequestParam TaskStatus status,
            @CurrentUser UserEntity user
    ) {
        TaskResponseDto updated = taskService.updateTaskStatus(boardId, taskId, status, user);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/{taskId}/assignees")
    public ResponseEntity<?> updateAssignees(
            @PathVariable("board_id")  Long boardId,
            @PathVariable("taskId") Long taskId,
            @RequestBody List<Long> assigneeIds,
            @CurrentUser UserEntity user
    ) {
        TaskResponseDto updated = taskService.updateAssignees(boardId, taskId, assigneeIds, user);
        return ResponseEntity.ok(updated);
    }
}
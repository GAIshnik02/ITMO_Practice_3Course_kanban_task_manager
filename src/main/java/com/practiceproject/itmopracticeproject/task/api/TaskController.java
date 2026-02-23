package com.practiceproject.itmopracticeproject.task.api;

import com.practiceproject.itmopracticeproject.task.TaskStatus;
import com.practiceproject.itmopracticeproject.task.domain.TaskCreateRequestDto;
import com.practiceproject.itmopracticeproject.task.domain.TaskResponseDto;
import com.practiceproject.itmopracticeproject.task.domain.TaskUpdateRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{id}/tasks")
public class TaskController {

    private final TaskService taskService;


    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getTasks(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getAllTasks(id));
    }

    @GetMapping("/{task_id}")
    public ResponseEntity<TaskResponseDto> getTask(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getTask(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
            @PathVariable("id") Long boardId,
            @RequestBody @Valid TaskCreateRequestDto dto

    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(boardId, dto));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable("id") Long boardId,
            @PathVariable("taskId") Long taskId,
            @RequestBody @Valid TaskUpdateRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(taskService.updateTask(boardId, taskId, dto));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable("id") Long boardId,
            @PathVariable("taskId") Long taskId
    ) {
        taskService.deleteTask(taskId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(
            @PathVariable("id") Long boardId,
            @PathVariable("taskId") Long taskId,
            @RequestParam TaskStatus status
    ) {
        TaskResponseDto updated = taskService.updateTaskStatus(taskId, status);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/{taskId}/assignees")
    public ResponseEntity<TaskResponseDto> updateAssignees(
            @PathVariable("taskId") Long taskId,
            @RequestBody List<Long> assigneeIds
    ) {
        TaskResponseDto updated = taskService.updateAssignees(taskId, assigneeIds);
        return ResponseEntity.ok(updated);
    }
}
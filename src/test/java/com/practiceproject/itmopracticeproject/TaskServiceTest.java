package com.practiceproject.itmopracticeproject;

import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberEntity;
import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberRepository;
import com.practiceproject.itmopracticeproject.board_members.dto.Role;
import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.boards.db.BoardRepository;
import com.practiceproject.itmopracticeproject.task.api.TaskService;
import com.practiceproject.itmopracticeproject.task.db.TaskEntity;
import com.practiceproject.itmopracticeproject.task.db.TaskRepository;
import com.practiceproject.itmopracticeproject.task.dto.*;
import com.practiceproject.itmopracticeproject.user.db.GlobalRole;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import com.practiceproject.itmopracticeproject.user.db.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardMemberRepository boardMemberRepository;

    @Mock
    private TaskMapper mapper;

    @InjectMocks
    private TaskService taskService;

    @Captor
    private ArgumentCaptor<TaskEntity> taskEntityCaptor;

    private UserEntity admin;
    private UserEntity owner;
    private UserEntity member;
    private UserEntity viewer;
    private UserEntity assignee1;
    private UserEntity assignee2;
    private BoardEntity testBoard;
    private BoardMemberEntity ownerMember;
    private BoardMemberEntity memberMember;
    private BoardMemberEntity viewerMember;
    private TaskEntity testTask;
    private TaskCreateRequestDto createRequest;
    private TaskUpdateRequestDto updateRequest;
    private TaskResponseDto testResponse;

    @BeforeEach
    void setUp() {
        admin = new UserEntity();
        admin.setId(1L);
        admin.setRole(GlobalRole.ADMIN);

        owner = new UserEntity();
        owner.setId(2L);
        owner.setRole(GlobalRole.USER);

        member = new UserEntity();
        member.setId(3L);
        member.setRole(GlobalRole.USER);

        viewer = new UserEntity();
        viewer.setId(4L);
        viewer.setRole(GlobalRole.USER);

        assignee1 = new UserEntity();
        assignee1.setId(5L);
        assignee1.setRole(GlobalRole.USER);

        assignee2 = new UserEntity();
        assignee2.setId(6L);
        assignee2.setRole(GlobalRole.USER);

        testBoard = new BoardEntity();
        testBoard.setId(100L);
        testBoard.setName("Test Board");

        ownerMember = new BoardMemberEntity();
        ownerMember.setBoard(testBoard);
        ownerMember.setUser(owner);
        ownerMember.setRole(Role.OWNER);

        memberMember = new BoardMemberEntity();
        memberMember.setBoard(testBoard);
        memberMember.setUser(member);
        memberMember.setRole(Role.MEMBER);

        viewerMember = new BoardMemberEntity();
        viewerMember.setBoard(testBoard);
        viewerMember.setUser(viewer);
        viewerMember.setRole(Role.VIEWER);

        testTask = new TaskEntity();
        testTask.setId(1000L);
        testTask.setBoard(testBoard);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.TODO);
        testTask.setPriority(TaskPriority.MEDIUM);
        testTask.setPosition(1);
        testTask.setCreator(owner);
        testTask.setAssignees(Set.of());

        createRequest = new TaskCreateRequestDto(
                "New Task",
                "New Description",
                TaskStatus.TODO,
                TaskPriority.HIGH,
                1,
                List.of(5L, 6L)
        );

        updateRequest = new TaskUpdateRequestDto(
                "Updated Task",
                "Updated Description",
                TaskStatus.IN_PROGRESS,
                TaskPriority.CRITICAL,
                2,
                List.of(5L)
        );

        testResponse = new TaskResponseDto(
                1000L, 100L, "Test Task", "Test Description",
                TaskStatus.TODO, TaskPriority.MEDIUM, 1, 2L, List.of(), null, null
        );
    }

    @Test
    void getAllTasks_AsMember_Success() {
        // Arrange
        when(boardRepository.existsById(100L)).thenReturn(true);
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardRepository.existsById(100L)).thenReturn(true);
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 3L)).thenReturn(Optional.of(memberMember));
        when(taskRepository.findAllByBoardIdWithAssignees(100L)).thenReturn(List.of(testTask));
        when(mapper.toDto(testTask)).thenReturn(testResponse);

        // Act
        List<TaskResponseDto> result = taskService.getAllTasks(100L, member);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(1000L);

        verify(boardRepository).findById(100L);
        verify(boardMemberRepository).findByBoardIdAndUserId(100L, 3L);
        verify(taskRepository).findAllByBoardIdWithAssignees(100L);
    }

    @Test
    void getAllTasks_AsAdmin_Success() {
        // Arrange
        when(boardRepository.existsById(100L)).thenReturn(true);
        when(taskRepository.findAllByBoardIdWithAssignees(100L)).thenReturn(List.of(testTask));
        when(mapper.toDto(testTask)).thenReturn(testResponse);

        // Act
        List<TaskResponseDto> result = taskService.getAllTasks(100L, admin);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(1000L);  // ← ID ЗАДАЧИ, а не доски!

        verify(boardRepository).existsById(100L);
        verify(boardRepository, never()).findById(any());  // ← для админа не вызывается!
        verify(boardMemberRepository, never()).findByBoardIdAndUserId(any(), any());
    }



    @Test
    void getAllTasks_AsNonMember_ThrowsSecurityException() {
        // Arrange
        UserEntity nonMember = new UserEntity();
        nonMember.setId(7L);
        nonMember.setRole(GlobalRole.USER);

        when(boardRepository.existsById(100L)).thenReturn(true);
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 7L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.getAllTasks(100L, nonMember))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You are not allowed to access this board!");
    }

    @Test
    void getAllTasks_BoardNotFound_ThrowsException() {
        // Arrange
        when(boardRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> taskService.getAllTasks(999L, member))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Board with id 999 not found");
    }

    @Test
    void getTask_AsMember_Success() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 3L)).thenReturn(Optional.of(memberMember));
        when(taskRepository.findByIdWithAssignees(1000L)).thenReturn(Optional.of(testTask));
        when(mapper.toDto(testTask)).thenReturn(testResponse);

        // Act
        TaskResponseDto result = taskService.getTask(100L, 1000L, member);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1000L);
    }

    @Test
    void createTask_AsOwner_Success() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(userRepository.findById(5L)).thenReturn(Optional.of(assignee1));
        when(userRepository.findById(6L)).thenReturn(Optional.of(assignee2));
        when(mapper.toEntity(eq(createRequest), eq(testBoard), eq(owner), any(Set.class)))
                .thenReturn(testTask);
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(mapper.toDto(testTask)).thenReturn(testResponse);

        // Act
        TaskResponseDto result = taskService.createTask(100L, createRequest, owner);

        // Assert
        assertThat(result).isNotNull();
        verify(taskRepository).save(taskEntityCaptor.capture());
        verify(boardMemberRepository).findByBoardIdAndUserId(100L, 2L);
    }

    @Test
    void createTask_AsMember_Success() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 3L)).thenReturn(Optional.of(memberMember));
        when(userRepository.findById(3L)).thenReturn(Optional.of(member));
        when(userRepository.findById(5L)).thenReturn(Optional.of(assignee1));
        when(userRepository.findById(6L)).thenReturn(Optional.of(assignee2));
        when(mapper.toEntity(eq(createRequest), eq(testBoard), eq(member), any(Set.class)))
                .thenReturn(testTask);
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(mapper.toDto(testTask)).thenReturn(testResponse);

        // Act
        TaskResponseDto result = taskService.createTask(100L, createRequest, member);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void createTask_AsViewer_ThrowsSecurityException() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 4L)).thenReturn(Optional.of(viewerMember));

        // Act & Assert
        assertThatThrownBy(() -> taskService.createTask(100L, createRequest, viewer))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You cannot add tasks to this board!");
    }

    @Test
    void createTask_BoardNotFound_ThrowsException() {
        // Arrange
        when(boardRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.createTask(999L, createRequest, owner))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Board with id 999 not found");
    }

    @Test
    void updateTask_AsOwner_Success() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(taskRepository.findByIdWithAssignees(1000L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(5L)).thenReturn(Optional.of(assignee1));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(mapper.toDto(testTask)).thenReturn(testResponse);

        // Act
        TaskResponseDto result = taskService.updateTask(100L, 1000L, updateRequest, owner);

        // Assert
        assertThat(result).isNotNull();
        assertThat(testTask.getTitle()).isEqualTo("Updated Task");
        assertThat(testTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(testTask.getPriority()).isEqualTo(TaskPriority.CRITICAL);
        assertThat(testTask.getPosition()).isEqualTo(2);

        verify(taskRepository).save(testTask);
    }

    @Test
    void updateTask_AsMember_Success() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 3L)).thenReturn(Optional.of(memberMember));
        when(taskRepository.findByIdWithAssignees(1000L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(5L)).thenReturn(Optional.of(assignee1));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(mapper.toDto(testTask)).thenReturn(testResponse);

        // Act
        TaskResponseDto result = taskService.updateTask(100L, 1000L, updateRequest, member);

        // Assert
        assertThat(result).isNotNull();
        verify(taskRepository).save(testTask);
    }

    @Test
    void updateTask_TaskNotInBoard_ThrowsException() {
        // Arrange
        BoardEntity otherBoard = new BoardEntity();
        otherBoard.setId(200L);
        testTask.setBoard(otherBoard);

        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(taskRepository.findByIdWithAssignees(1000L)).thenReturn(Optional.of(testTask));

        // Act & Assert
        assertThatThrownBy(() -> taskService.updateTask(100L, 1000L, updateRequest, owner))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task does not belong to this board");
    }

    @Test
    void deleteTask_AsOwner_Success() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(taskRepository.existsById(1000L)).thenReturn(true);

        // Act
        taskService.deleteTask(100L, 1000L, owner);

        // Assert
        verify(taskRepository).deleteById(1000L);
    }

    @Test
    void deleteTask_AsMember_Success() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 3L)).thenReturn(Optional.of(memberMember));
        when(taskRepository.existsById(1000L)).thenReturn(true);

        // Act
        taskService.deleteTask(100L, 1000L, member);

        // Assert
        verify(taskRepository).deleteById(1000L);
    }

    @Test
    void deleteTask_AsViewer_ThrowsSecurityException() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 4L)).thenReturn(Optional.of(viewerMember));

        // Act & Assert
        assertThatThrownBy(() -> taskService.deleteTask(100L, 1000L, viewer))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You cannot delete this task!");
    }

    @Test
    void deleteTask_TaskNotFound_ThrowsException() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(taskRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> taskService.deleteTask(100L, 999L, owner))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task with id 999 not found");
    }

    @Test
    void updateTaskStatus_AsMember_Success() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 3L)).thenReturn(Optional.of(memberMember));
        when(taskRepository.findById(1000L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(mapper.toDto(testTask)).thenReturn(testResponse);

        // Act
        TaskResponseDto result = taskService.updateTaskStatus(100L, 1000L, TaskStatus.DONE, member);

        // Assert
        assertThat(result).isNotNull();
        assertThat(testTask.getStatus()).isEqualTo(TaskStatus.DONE);
        verify(taskRepository).save(testTask);
    }

    @Test
    void updateTaskStatus_AsOwner_Success() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(taskRepository.findById(1000L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(mapper.toDto(testTask)).thenReturn(testResponse);

        // Act
        TaskResponseDto result = taskService.updateTaskStatus(100L, 1000L, TaskStatus.DONE, owner);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void updateAssignees_AsOwner_Success() {
        // Arrange
        List<Long> newAssigneeIds = List.of(5L, 6L);

        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(taskRepository.findByIdWithAssignees(1000L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(5L)).thenReturn(Optional.of(assignee1));
        when(userRepository.findById(6L)).thenReturn(Optional.of(assignee2));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(mapper.toDto(testTask)).thenReturn(testResponse);

        // Act
        TaskResponseDto result = taskService.updateAssignees(100L, 1000L, newAssigneeIds, owner);

        // Assert
        assertThat(result).isNotNull();
        verify(taskRepository).save(taskEntityCaptor.capture());
        TaskEntity savedTask = taskEntityCaptor.getValue();
        assertThat(savedTask.getAssignees()).hasSize(2);
    }

    @Test
    void updateAssignees_AsMember_Success() {
        // Arrange
        List<Long> newAssigneeIds = List.of(5L);

        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 3L)).thenReturn(Optional.of(memberMember));
        when(taskRepository.findByIdWithAssignees(1000L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(5L)).thenReturn(Optional.of(assignee1));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(mapper.toDto(testTask)).thenReturn(testResponse);

        // Act
        TaskResponseDto result = taskService.updateAssignees(100L, 1000L, newAssigneeIds, member);

        // Assert
        assertThat(result).isNotNull();
        verify(taskRepository).save(taskEntityCaptor.capture());
        TaskEntity savedTask = taskEntityCaptor.getValue();
        assertThat(savedTask.getAssignees()).hasSize(1);
    }

    @Test
    void updateAssignees_AsViewer_ThrowsSecurityException() {
        // Arrange
        List<Long> newAssigneeIds = List.of(5L);

        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 4L)).thenReturn(Optional.of(viewerMember));

        // Act & Assert
        assertThatThrownBy(() -> taskService.updateAssignees(100L, 1000L, newAssigneeIds, viewer))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You cannot update this task!");
    }

    @Test
    void checkBoardAccess_WithAdmin_DoesNotThrow() {
        // Arrange
        when(boardRepository.existsById(100L)).thenReturn(true);
        when(taskRepository.findAllByBoardIdWithAssignees(100L)).thenReturn(List.of(testTask));
        when(mapper.toDto(testTask)).thenReturn(testResponse);

        // Act
        List<TaskResponseDto> result = taskService.getAllTasks(100L, admin);

        // Assert
        assertThat(result).isNotNull();
        verify(boardRepository).existsById(100L);
        verify(boardRepository, never()).findById(any());  // ← для админа не вызывается!
        verify(boardMemberRepository, never()).findByBoardIdAndUserId(any(), any());
    }
}
package com.practiceproject.itmopracticeproject;

import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberEntity;
import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberRepository;
import com.practiceproject.itmopracticeproject.board_members.dto.Role;
import com.practiceproject.itmopracticeproject.boards.api.BoardService;
import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.boards.db.BoardRepository;
import com.practiceproject.itmopracticeproject.boards.dto.BoardMapper;
import com.practiceproject.itmopracticeproject.boards.dto.BoardRequestDto;
import com.practiceproject.itmopracticeproject.boards.dto.BoardResponseDto;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardMemberRepository boardMemberRepository;

    @Mock
    private BoardMapper mapper;

    @InjectMocks
    private BoardService boardService;

    @Captor
    private ArgumentCaptor<BoardEntity> boardEntityCaptor;

    @Captor
    private ArgumentCaptor<BoardMemberEntity> boardMemberEntityCaptor;

    private UserEntity testUser;
    private BoardEntity testBoard;
    private BoardRequestDto testRequest;
    private BoardResponseDto testResponse;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setLogin("testuser");
        testUser.setRole(GlobalRole.USER);

        testBoard = new BoardEntity();
        testBoard.setId(1L);
        testBoard.setName("Test Board");
        testBoard.setDescription("Test Description");
        testBoard.setOwner(testUser);

        testRequest = new BoardRequestDto("Test Board", "Test Description");
        testResponse = new BoardResponseDto(1L, "Test Board", "Test Description", 1L, null, null);
    }

    @Test
    void createBoard_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(boardRepository.save(any(BoardEntity.class))).thenReturn(testBoard);
        when(boardMemberRepository.save(any(BoardMemberEntity.class))).thenReturn(null);
        when(mapper.toDto(any(BoardEntity.class))).thenReturn(testResponse);

        // Act
        BoardResponseDto result = boardService.createBoard(testRequest, 1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Board");

        verify(boardRepository).save(boardEntityCaptor.capture());
        BoardEntity savedBoard = boardEntityCaptor.getValue();
        assertThat(savedBoard.getName()).isEqualTo("Test Board");
        assertThat(savedBoard.getOwner()).isEqualTo(testUser);

        verify(boardMemberRepository).save(boardMemberEntityCaptor.capture());
        BoardMemberEntity savedMember = boardMemberEntityCaptor.getValue();
        assertThat(savedMember.getRole()).isEqualTo(Role.OWNER);
        assertThat(savedMember.getBoard()).isEqualTo(testBoard);
        assertThat(savedMember.getUser()).isEqualTo(testUser);
    }

    @Test
    void createBoard_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> boardService.createBoard(testRequest, 999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id: 999 not found");

        verify(boardRepository, never()).save(any());
        verify(boardMemberRepository, never()).save(any());
    }

    @Test
    void getBoardById_AsAdmin_Success() {
        // Arrange
        UserEntity admin = new UserEntity();
        admin.setId(2L);
        admin.setRole(GlobalRole.ADMIN);

        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));
        when(mapper.toDto(testBoard)).thenReturn(testResponse);

        // Act
        BoardResponseDto result = boardService.getBoardById(1L, admin);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(boardMemberRepository, never()).findByBoardIdAndUserId(any(), any());
    }

    @Test
    void getBoardById_AsMember_Success() {
        // Arrange
        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(new BoardMemberEntity()));
        when(mapper.toDto(testBoard)).thenReturn(testResponse);

        // Act
        BoardResponseDto result = boardService.getBoardById(1L, testUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getBoardById_AsNonMember_ThrowsSecurityException() {
        // Arrange
        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> boardService.getBoardById(1L, testUser))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You don't have permission to access this board!");
    }

    @Test
    void getBoardById_BoardNotFound_ThrowsException() {
        // Arrange
        when(boardRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> boardService.getBoardById(999L, testUser))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Board with id: 999 not found");
    }

    @Test
    void deleteBoardById_AsAdmin_Success() {
        // Arrange
        UserEntity admin = new UserEntity();
        admin.setId(2L);
        admin.setRole(GlobalRole.ADMIN);

        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));

        // Act
        boardService.deleteBoardById(1L, admin);

        // Assert
        verify(boardRepository).delete(testBoard);
        verify(boardMemberRepository, never()).findByBoardIdAndUserId(any(), any());
    }

    @Test
    void deleteBoardById_AsOwner_Success() {
        // Arrange
        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(new BoardMemberEntity()));

        // Act
        boardService.deleteBoardById(1L, testUser);

        // Assert
        verify(boardRepository).delete(testBoard);
    }

    @Test
    void deleteBoardById_AsNonOwner_ThrowsSecurityException() {
        // Arrange
        UserEntity otherUser = new UserEntity();
        otherUser.setId(2L);
        otherUser.setRole(GlobalRole.USER);

        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> boardService.deleteBoardById(1L, otherUser))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You don't have permission to access this board!");
    }
}
package com.practiceproject.itmopracticeproject;

import com.practiceproject.itmopracticeproject.board_members.api.BoardMemberService;
import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberEntity;
import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberId;
import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberRepository;
import com.practiceproject.itmopracticeproject.board_members.dto.BoardMemberMapper;
import com.practiceproject.itmopracticeproject.board_members.dto.BoardMemberResponseDto;
import com.practiceproject.itmopracticeproject.board_members.dto.CreateBoardMemberRequest;
import com.practiceproject.itmopracticeproject.board_members.dto.Role;
import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.boards.db.BoardRepository;
import com.practiceproject.itmopracticeproject.user.db.GlobalRole;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import com.practiceproject.itmopracticeproject.user.db.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardMemberServiceTest {

    @Mock
    private BoardMemberRepository boardMemberRepository;

    @Mock
    private BoardMemberMapper mapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardMemberService boardMemberService;

    @Captor
    private ArgumentCaptor<BoardMemberEntity> boardMemberEntityCaptor;

    private UserEntity admin;
    private UserEntity owner;
    private UserEntity member;
    private UserEntity viewer;
    private UserEntity newUser;
    private BoardEntity testBoard;
    private BoardMemberEntity ownerMember;
    private BoardMemberEntity memberMember;
    private CreateBoardMemberRequest addMemberRequest;

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

        newUser = new UserEntity();
        newUser.setId(5L);
        newUser.setRole(GlobalRole.USER);

        testBoard = new BoardEntity();
        testBoard.setId(100L);
        testBoard.setName("Test Board");

        ownerMember = new BoardMemberEntity();
        ownerMember.setId(new BoardMemberId(100L, 2L));
        ownerMember.setBoard(testBoard);
        ownerMember.setUser(owner);
        ownerMember.setRole(Role.OWNER);

        memberMember = new BoardMemberEntity();
        memberMember.setId(new BoardMemberId(100L, 3L));
        memberMember.setBoard(testBoard);
        memberMember.setUser(member);
        memberMember.setRole(Role.MEMBER);

        addMemberRequest = new CreateBoardMemberRequest(5L, Role.VIEWER);
    }

    @Test
    void addMember_AsAdmin_Success() {
        // Arrange
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(userRepository.findById(5L)).thenReturn(Optional.of(newUser));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 5L)).thenReturn(Optional.empty());
        when(mapper.toEntity(testBoard, newUser, addMemberRequest)).thenReturn(new BoardMemberEntity());
        when(boardMemberRepository.save(any(BoardMemberEntity.class))).thenReturn(new BoardMemberEntity());
        when(mapper.toDto(any(BoardMemberEntity.class))).thenReturn(new BoardMemberResponseDto(100L, 5L, Role.VIEWER, null, null));

        // Act
        BoardMemberResponseDto result = boardMemberService.addMember(100L, addMemberRequest, admin);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(5L);
        assertThat(result.role()).isEqualTo(Role.VIEWER);
        verify(boardMemberRepository).save(any(BoardMemberEntity.class));
    }

    @Test
    void addMember_AsOwner_Success() {
        // Arrange
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(userRepository.findById(5L)).thenReturn(Optional.of(newUser));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 5L)).thenReturn(Optional.empty());
        when(mapper.toEntity(testBoard, newUser, addMemberRequest)).thenReturn(new BoardMemberEntity());
        when(boardMemberRepository.save(any(BoardMemberEntity.class))).thenReturn(new BoardMemberEntity());
        when(mapper.toDto(any(BoardMemberEntity.class))).thenReturn(new BoardMemberResponseDto(100L, 5L, Role.VIEWER, null, null));

        // Act
        BoardMemberResponseDto result = boardMemberService.addMember(100L, addMemberRequest, owner);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void addMember_AsMember_Success() {
        // Arrange
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 3L)).thenReturn(Optional.of(memberMember));
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(userRepository.findById(5L)).thenReturn(Optional.of(newUser));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 5L)).thenReturn(Optional.empty());
        when(mapper.toEntity(testBoard, newUser, addMemberRequest)).thenReturn(new BoardMemberEntity());
        when(boardMemberRepository.save(any(BoardMemberEntity.class))).thenReturn(new BoardMemberEntity());
        when(mapper.toDto(any(BoardMemberEntity.class))).thenReturn(new BoardMemberResponseDto(100L, 5L, Role.VIEWER, null, null));

        // Act
        BoardMemberResponseDto result = boardMemberService.addMember(100L, addMemberRequest, member);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void addMember_AsViewer_ThrowsSecurityException() {
        // Arrange
        BoardMemberEntity viewerMember = new BoardMemberEntity();
        viewerMember.setId(new BoardMemberId(100L, 4L));
        viewerMember.setRole(Role.VIEWER);

        when(boardMemberRepository.findByBoardIdAndUserId(100L, 4L)).thenReturn(Optional.of(viewerMember));

        // Act & Assert
        assertThatThrownBy(() -> boardMemberService.addMember(100L, addMemberRequest, viewer))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You cannot add new members to this board!");
    }

    @Test
    void addMember_UserAlreadyMember_Rejoins() {
        // Arrange
        BoardMemberEntity existingMember = new BoardMemberEntity();
        existingMember.setId(new BoardMemberId(100L, 5L));
        existingMember.setBoard(testBoard);
        existingMember.setUser(newUser);
        existingMember.setRole(Role.VIEWER);
        existingMember.leave(); // Устанавливаем left_at

        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(userRepository.findById(5L)).thenReturn(Optional.of(newUser));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 5L)).thenReturn(Optional.of(existingMember));
        when(mapper.toEntity(testBoard, newUser, addMemberRequest)).thenReturn(new BoardMemberEntity());
        when(boardMemberRepository.save(any(BoardMemberEntity.class))).thenReturn(new BoardMemberEntity());
        when(mapper.toDto(any(BoardMemberEntity.class))).thenReturn(new BoardMemberResponseDto(100L, 5L, Role.VIEWER, null, null));

        // Act
        BoardMemberResponseDto result = boardMemberService.addMember(100L, addMemberRequest, owner);

        // Assert
        assertThat(result).isNotNull();
        verify(mapper).toEntity(testBoard, newUser, addMemberRequest);
        verify(boardMemberRepository).save(boardMemberEntityCaptor.capture());
        BoardMemberEntity savedEntity = boardMemberEntityCaptor.getValue();
        assertThat(savedEntity.isActive()).isTrue(); // rejoin должен сбросить left_at
    }

    @Test
    void getMembers_AsMember_Success() {
        // Arrange
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findAllByBoardId(100L)).thenReturn(List.of(ownerMember, memberMember));

        BoardMemberResponseDto ownerDto = new BoardMemberResponseDto(100L, 2L, Role.OWNER, null, null);
        BoardMemberResponseDto memberDto = new BoardMemberResponseDto(100L, 3L, Role.MEMBER, null, null);

        when(mapper.toDto(ownerMember)).thenReturn(ownerDto);
        when(mapper.toDto(memberMember)).thenReturn(memberDto);

        // Act
        List<BoardMemberResponseDto> result = boardMemberService.getMembers(100L, owner);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).userId()).isEqualTo(2L);
        assertThat(result.get(1).userId()).isEqualTo(3L);
    }

    @Test
    void updateMember_AsOwner_Success() {
        // Arrange
        CreateBoardMemberRequest updateRequest = new CreateBoardMemberRequest(3L, Role.OWNER);

        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(userRepository.findById(3L)).thenReturn(Optional.of(member));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 3L)).thenReturn(Optional.of(memberMember));
        when(boardMemberRepository.save(any(BoardMemberEntity.class))).thenReturn(memberMember);

        BoardMemberResponseDto updatedDto = new BoardMemberResponseDto(100L, 3L, Role.OWNER, null, null);
        when(mapper.toDto(memberMember)).thenReturn(updatedDto);

        // Act
        BoardMemberResponseDto result = boardMemberService.updateMember(100L, updateRequest, owner);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(Role.OWNER);
        verify(mapper).updateRole(memberMember, Role.OWNER);
    }

    @Test
    void updateMember_UpdateOwnRole_ThrowsSecurityException() {
        // Arrange
        CreateBoardMemberRequest updateRequest = new CreateBoardMemberRequest(2L, Role.MEMBER);

        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));

        // Act & Assert
        assertThatThrownBy(() -> boardMemberService.updateMember(100L, updateRequest, owner))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You cannot update your own role!");
    }

    @Test
    void deleteMember_AsOwner_Success() {
        // Arrange
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMember));
        when(userRepository.findById(3L)).thenReturn(Optional.of(member));
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 3L)).thenReturn(Optional.of(memberMember));

        // Act
        boardMemberService.deleteMemberFromBoardById(100L, 3L, owner);

        // Assert
        verify(boardMemberRepository).save(memberMember);
        assertThat(memberMember.getLeft_at()).isNotNull();
    }

    @Test
    void deleteMember_MemberTriesToDeleteOwner_ThrowsSecurityException() {
        // Arrange
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 3L)).thenReturn(Optional.of(memberMember));
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(boardRepository.findById(100L)).thenReturn(Optional.of(testBoard));

        BoardMemberEntity ownerMemberEntity = new BoardMemberEntity();
        ownerMemberEntity.setRole(Role.OWNER);
        when(boardMemberRepository.findByBoardIdAndUserId(100L, 2L)).thenReturn(Optional.of(ownerMemberEntity));

        // Act & Assert
        assertThatThrownBy(() -> boardMemberService.deleteMemberFromBoardById(100L, 2L, member))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You cannot delete this user!");
    }
}
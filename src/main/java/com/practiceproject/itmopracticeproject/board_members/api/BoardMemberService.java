package com.practiceproject.itmopracticeproject.board_members.api;

import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberEntity;
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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BoardMemberService {

    private final BoardMemberRepository boardMemberRepository;
    private final BoardMemberMapper mapper;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public BoardMemberService(
            BoardMemberRepository boardMemberRepository,
            BoardMemberMapper mapper,
            UserRepository userRepository,
            BoardRepository boardRepository
    ) {
        this.boardMemberRepository = boardMemberRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    public BoardMemberResponseDto addMember(
            Long boardId,
            CreateBoardMemberRequest request,
            UserEntity user
    ) {
        // If adding user is in the board
        BoardMemberEntity boardMemberEntity = checkBoardAccess(boardId, user);

        if (boardMemberEntity != null) {
            if (!(boardMemberEntity.getRole().equals(Role.OWNER) || boardMemberEntity.getRole().equals(Role.MEMBER))) {
                throw new SecurityException("You cannot add new members to this board!");
            }
        }

        UserEntity userToAddEntity = userRepository.findById(request.userId()).orElseThrow(
                () -> new EntityNotFoundException("User to add with Id: " + request.userId() + " not found")
        );

        BoardEntity boardEntity = boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException("Board: " + boardId + " not found")
        );

        Optional<BoardMemberEntity> existingBoardMemberEntity = boardMemberRepository.findByBoardIdAndUserId(
                boardEntity.getId(), userToAddEntity.getId()
        );

        var entityToSave = mapper.toEntity(boardEntity, userToAddEntity, request);
        entityToSave.setRole(Role.VIEWER);

        if (existingBoardMemberEntity.isPresent()) {
            entityToSave.rejoin();
        }

        var savedEntity = boardMemberRepository.save(entityToSave);

        return mapper.toDto(savedEntity);
    }

    public List<BoardMemberResponseDto> getMembers(
            Long boardId,
            UserEntity user
    ) {
        checkBoardAccess(boardId, user);

        boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException("Board: " + boardId + " not found")
        );

        List<BoardMemberEntity> entities = boardMemberRepository.findAllByBoardId(
                boardId
        );

        return entities.stream().map(mapper::toDto).toList();
    }

    public BoardMemberResponseDto updateMember(
            Long boardId,
            CreateBoardMemberRequest request,
            UserEntity currentUser
    ) {
        BoardMemberEntity currentUserMember = checkBoardAccess(boardId, currentUser);

        validateUpdatePermissions(currentUserMember, request, currentUser);

        boardRepository.findById(boardId)
                       .orElseThrow(() -> new EntityNotFoundException("Board: " + boardId + " not found"));

        userRepository.findById(request.userId())
                      .orElseThrow(() -> new EntityNotFoundException("User: " + request.userId() + " not found"));

        BoardMemberEntity userEntityToUpdate = boardMemberRepository
                .findByBoardIdAndUserId(boardId, request.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User: " + request.userId() + " does not belong to this board!"
                ));

        if (currentUser.getId().equals(request.userId())) {
            throw new SecurityException("You cannot update your own role!");
        }

        mapper.updateRole(userEntityToUpdate, request.role());
        var savedEntity = boardMemberRepository.save(userEntityToUpdate);

        return mapper.toDto(savedEntity);
    }


    public void deleteMemberFromBoardById(
            Long boardId,
            Long userId,
            UserEntity currentUser
    ) {
        BoardMemberEntity currentUserEntity = checkBoardAccess(boardId, currentUser);

        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User: " + userId + " not found")
        );
        boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException("Board: " + boardId + " not found")
        );

        BoardMemberEntity entityToDelete = boardMemberRepository
                .findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Member not found for board: " + boardId + " and user: " + userId
                        ));

        validateDeletePermissions(currentUserEntity, currentUser, entityToDelete);
        entityToDelete.leave();
        boardMemberRepository.save(entityToDelete);
    }

    private BoardMemberEntity checkBoardAccess(Long boardId, UserEntity user) {
        if (user.getRole().equals(GlobalRole.ADMIN)) {
            return null; // Админ может всё, но не обязательно является участником
        }

        return boardMemberRepository.findByBoardIdAndUserId(boardId, user.getId()).orElseThrow(
                () -> new SecurityException("You are not allowed to access this board!")
        );
    }

    private void validateUpdatePermissions(
            BoardMemberEntity currentUserMember,
            CreateBoardMemberRequest request,
            UserEntity currentUser
    ) {

        if (currentUser.getRole().equals(GlobalRole.ADMIN)) {
            return;
        }

        if (currentUserMember == null) {
            throw new SecurityException("You are not allowed to access this board!");
        }

        Role currentRole = currentUserMember.getRole();
        Role newRole = request.role();

        if (!(currentRole.equals(Role.OWNER) || currentRole.equals(Role.MEMBER))) {
            throw new SecurityException("You cannot update members of this board!");
        }

        if (currentRole.equals(Role.MEMBER) && newRole.equals(Role.OWNER)) {
            throw new SecurityException("Only owner can assign OWNER role!");
        }
    }

    private void validateDeletePermissions(
            BoardMemberEntity currentUserMember,
            UserEntity currentUser,
            BoardMemberEntity userToDelete
    ) {
        if (currentUser.getRole().equals(GlobalRole.ADMIN)) {
            return;
        }

        if (currentUserMember == null) {
            throw new SecurityException("You cannot delete members of this board!");
        }

        Role currentRole = currentUserMember.getRole();

        if (!(currentRole.equals(Role.OWNER) || currentRole.equals(Role.MEMBER))) {
            throw new SecurityException("You cannot delete members of this board!");
        }

        if (currentRole.equals(Role.MEMBER) && userToDelete.getRole().equals(Role.OWNER)) {
            throw new SecurityException("You cannot delete this user!");
        }
    }
}

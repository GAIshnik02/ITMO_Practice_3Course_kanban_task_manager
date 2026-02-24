package com.practiceproject.itmopracticeproject.boards.api;

import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberEntity;
import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberId;
import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberRepository;
import com.practiceproject.itmopracticeproject.board_members.dto.Role;
import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.boards.db.BoardRepository;
import com.practiceproject.itmopracticeproject.boards.dto.BoardRequestDto;
import com.practiceproject.itmopracticeproject.boards.dto.BoardMapper;
import com.practiceproject.itmopracticeproject.boards.dto.BoardResponseDto;
import com.practiceproject.itmopracticeproject.user.db.GlobalRole;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import com.practiceproject.itmopracticeproject.user.db.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;


@Controller
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final BoardMapper mapper;


    public BoardService(BoardRepository repository, UserRepository userRepository, BoardMapper mapper, BoardMemberRepository boardMemberRepository) {
        this.boardRepository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.boardMemberRepository = boardMemberRepository;
    }

    public BoardResponseDto createBoard(
            BoardRequestDto request,
            Long ownerId
    ) {
        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + ownerId + " not found"
                ));

        var boardToCreate = new BoardEntity();
        boardToCreate.setName(request.name());
        boardToCreate.setDescription(request.description());
        boardToCreate.setOwner(owner);

        var savedBoard = boardRepository.save(boardToCreate);

        BoardMemberId memberId = new BoardMemberId(savedBoard.getId(), owner.getId());

        // добавляем юзера в участники доски при создании
        var boardMemberEntity = new BoardMemberEntity();
        boardMemberEntity.setId(memberId);
        boardMemberEntity.setBoard(savedBoard);
        boardMemberEntity.setUser(owner);
        boardMemberEntity.setRole(Role.OWNER);

        boardMemberRepository.save(boardMemberEntity);

        return mapper.toDto(savedBoard);
    }

    public BoardResponseDto getBoardById(Long boardId, UserEntity user) {
        if (user.getRole().equals(GlobalRole.ADMIN)) {
            BoardEntity entity = boardRepository.findById(boardId).orElseThrow(() -> new
                    EntityNotFoundException("Board with id: " + boardId + " not found"));

            return mapper.toDto(entity);
        }

        BoardEntity entity = boardRepository.findById(boardId).orElseThrow(() -> new
                EntityNotFoundException("Board with id: " + boardId + " not found"));


        boardMemberRepository.findByBoardIdAndUserId(boardId, user.getId()).orElseThrow(
                    () -> new SecurityException("You don't have permission to access this board!"
                ));

        return mapper.toDto(entity);
    }

    public BoardResponseDto updateBoard(Long boardId , BoardRequestDto request,  UserEntity user) {
        if (user.getRole().equals(GlobalRole.ADMIN)) {
            BoardEntity entity = boardRepository.findById(boardId).orElseThrow(() -> new
                    EntityNotFoundException("Board with id: " + boardId + " not found"));

            return mapper.toDto(entity);
        }

        boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("Board with id: " + boardId + " not found"));

        UserEntity owner = userRepository.findById(user.getId()).orElseThrow(
                () -> new EntityNotFoundException("User with id: " + user.getId() + " not found"));

        boardMemberRepository.findByBoardIdAndUserId(boardId, user.getId()).orElseThrow(
                () -> new SecurityException("You don't have permission to access this board!"
                ));

        var boardToUpdate = new BoardEntity();
        boardToUpdate.setName(request.name());
        boardToUpdate.setDescription(request.description());
        boardToUpdate.setOwner(owner);

        var savedBoard = boardRepository.save(boardToUpdate);

        return mapper.toDto(savedBoard);
    }

    public void deleteBoardById(Long boardId, UserEntity user) {
        if (user.getRole().equals(GlobalRole.ADMIN)) {
            BoardEntity entity = boardRepository.findById(boardId).orElseThrow(() -> new
                    EntityNotFoundException("Board with id: " + boardId + " not found"));
            boardRepository.delete(entity);
            return;
        }

        BoardEntity entity = boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException("Board with id: " + boardId + " not found")
        );

        boardMemberRepository.findByBoardIdAndUserId(boardId, user.getId()).orElseThrow(
                () -> new SecurityException("You don't have permission to access this board!"
                ));

        boardRepository.delete(entity);
    }
}

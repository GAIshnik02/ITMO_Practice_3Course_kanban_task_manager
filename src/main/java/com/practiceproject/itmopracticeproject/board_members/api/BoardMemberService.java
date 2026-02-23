package com.practiceproject.itmopracticeproject.board_members.api;

import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberEntity;
import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberRepository;
import com.practiceproject.itmopracticeproject.board_members.domain.BoardMemberDto;
import com.practiceproject.itmopracticeproject.board_members.domain.BoardMemberMapper;
import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.boards.db.BoardRepository;
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

    public BoardMemberService(BoardMemberRepository boardMemberRepository, BoardMemberMapper mapper, UserRepository userRepository, BoardRepository boardRepository) {
        this.boardMemberRepository = boardMemberRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    public BoardMemberDto addMember(Long id, CreateBoardMemberRequest request) {
        UserEntity userEntity = userRepository.findById(request.userId()).orElseThrow(
                () -> new EntityNotFoundException("User: " + request.userId() + " not found")
        );
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Board: " + id + " not found")
        );
        Optional<BoardMemberEntity> boardMemberEntity = boardMemberRepository.findByBoardIdAndUserId(
                boardEntity.getId(), userEntity.getId()
        );

        var entityToSave = mapper.toEntity(boardEntity, userEntity,  request);

        if (boardMemberEntity.isPresent()) {
            entityToSave.rejoin();
        }

        var savedEntity = boardMemberRepository.save(entityToSave);

        return mapper.toDto(savedEntity);
    }

    public List<BoardMemberDto> getMembers(Long id) {
        boardRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Board: " + id + " not found")
        );

        List<BoardMemberEntity> entities = boardMemberRepository.findAllByBoardId(
            id
        );

        return entities.stream().map(mapper::toDto).toList();
    }

    public BoardMemberDto updateMember(Long id, CreateBoardMemberRequest request) {
        UserEntity userEntity = userRepository.findById(request.userId()).orElseThrow(
                () -> new EntityNotFoundException("User: " + request.userId() + " not found")
        );
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Board: " + id + " not found")
        );

        var entityToUpdate = mapper.toEntity(boardEntity, userEntity, request);

        var savedEntity = boardMemberRepository.save(entityToUpdate);

        return mapper.toDto(savedEntity);
    }

    public void deleteMemberFromBoardById(Long id, Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User: " + userId + " not found")
        );
        boardRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Board: " + id + " not found")
        );

        BoardMemberEntity entityToDelete = boardMemberRepository
                .findByBoardIdAndUserId(id, userId).orElseThrow(
                        () -> new EntityNotFoundException("Member not found for board: " + id + " and user: " + userId
                        ));
        entityToDelete.leave();
        mapper.markAsLeft(entityToDelete);
        boardMemberRepository.save(entityToDelete);
    }
}

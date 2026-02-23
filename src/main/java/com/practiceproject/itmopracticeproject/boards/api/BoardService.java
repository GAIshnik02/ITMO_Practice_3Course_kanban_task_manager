package com.practiceproject.itmopracticeproject.boards.api;

import com.practiceproject.itmopracticeproject.board_members.api.BoardMemberService;
import com.practiceproject.itmopracticeproject.board_members.api.CreateBoardMemberRequest;
import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberRepository;
import com.practiceproject.itmopracticeproject.board_members.domain.Role;
import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.boards.db.BoardRepository;
import com.practiceproject.itmopracticeproject.boards.domain.BoardDto;
import com.practiceproject.itmopracticeproject.boards.domain.BoardMapper;
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
    private final BoardMemberService boardMemberService;
    private final BoardMapper mapper;


    public BoardService(BoardRepository repository, UserRepository userRepository, BoardMemberService boardMemberRepository, BoardMapper mapper, BoardMemberService boardMemberService) {
        this.boardRepository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.boardMemberService = boardMemberService;
    }

    public BoardDto createBoard(BoardDto boardDto) {
        UserEntity owner = userRepository.findById(boardDto.owner_id())
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + boardDto.owner_id() + " not found"
                ));

        var boardToCreate = mapper.toBoardEntity(boardDto, owner);

        // добавляем юзера в участники доски при создании
        var requestToCreateMember = new CreateBoardMemberRequest(
                boardDto.owner_id(),
                Role.OWNER
        );


        var savedBoard = boardRepository.save(boardToCreate);

        boardMemberService.addMember(savedBoard.getId(),  requestToCreateMember);

        return mapper.toBoardDto(savedBoard);
    }

    public BoardDto getBoardById(Long id) {
        BoardEntity entity = boardRepository.findById(id).orElseThrow(() -> new
                EntityNotFoundException("Board with id: " + id + " not found"));

        return mapper.toBoardDto(entity);
    }

    public BoardDto updateBoard(Long id ,BoardDto boardDto) {
        boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Board with id: " + id + " not found"));

        UserEntity owner = userRepository.findById(boardDto.owner_id()).orElseThrow(
                () -> new EntityNotFoundException("User with id: " + boardDto.owner_id() + " not found"));
        var boardToUpdate = mapper.toBoardEntity(boardDto, owner);

        var savedBoard = boardRepository.save(boardToUpdate);

        return mapper.toBoardDto(savedBoard);
    }

    public void deleteBoardById(Long id) {
        BoardEntity entity = boardRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Board with id: " + id + " not found")
        );
        boardRepository.delete(entity);
    }
}

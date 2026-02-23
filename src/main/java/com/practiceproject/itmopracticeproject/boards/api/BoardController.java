package com.practiceproject.itmopracticeproject.boards.api;

import com.practiceproject.itmopracticeproject.boards.domain.BoardDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<BoardDto> createBoard(
            @Valid @RequestBody BoardDto boardDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createBoard(boardDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> getBoard(
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getBoardById(id));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<BoardDto> updateBoard(
            @Valid @RequestBody BoardDto boardDto,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.updateBoard(id, boardDto));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable("id") Long id
    ) {
        service.deleteBoardById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}

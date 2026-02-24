package com.practiceproject.itmopracticeproject.boards.api;

import com.practiceproject.itmopracticeproject.boards.dto.BoardRequestDto;
import com.practiceproject.itmopracticeproject.security.CurrentUser;
import com.practiceproject.itmopracticeproject.user.db.GlobalRole;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/boards")
public class AdminBoardController {

    private final BoardService service;

    public AdminBoardController(BoardService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBoard(
            @Valid @RequestBody BoardRequestDto request,
            @CurrentUser UserEntity user) {
        if (user.getRole() != GlobalRole.ADMIN) {
            throw new SecurityException("Access denied");
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createBoard(request, user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBoardById(
            @PathVariable("id") Long boardId,
            @CurrentUser UserEntity user) {
        if (user.getRole() != GlobalRole.ADMIN) {
            throw new SecurityException("Access denied");
        }

        return ResponseEntity.status(HttpStatus.OK).body(service.getBoardById(boardId, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoard(
            @Valid @RequestBody BoardRequestDto request,
            @PathVariable("id") Long id,
            @CurrentUser UserEntity user
    ) {
        if (user.getRole() != GlobalRole.ADMIN) {
            throw new SecurityException("Access denied");
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.updateBoard(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(
            @PathVariable("id") Long id,
            @CurrentUser UserEntity user
    ) {
        if (user.getRole() != GlobalRole.ADMIN) {
            throw new SecurityException("Access denied");
        }
        service.deleteBoardById(id, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}

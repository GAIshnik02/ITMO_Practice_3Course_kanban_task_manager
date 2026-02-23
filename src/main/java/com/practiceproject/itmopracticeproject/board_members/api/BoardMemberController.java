package com.practiceproject.itmopracticeproject.board_members.api;

import com.practiceproject.itmopracticeproject.board_members.domain.BoardMemberDto;
import jakarta.validation.Valid;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/")
public class BoardMemberController {

    private final BoardMemberService service;

    public BoardMemberController(BoardMemberService service) {
        this.service = service;
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<BoardMemberDto> addMember(
            @Valid @RequestBody CreateBoardMemberRequest request,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addMember(id, request));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<BoardMemberDto>> getMembers(
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getMembers(id));
    }

    @PutMapping("/{id}/members")
    public ResponseEntity<BoardMemberDto> updateMember(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreateBoardMemberRequest request
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.updateMember(id, request));
    }

    @DeleteMapping("/{id}/members/{user_id}")
    public ResponseEntity<Void> deleteMember(
        @PathVariable("id") Long id,
        @PathVariable("user_id") Long user_id
    ) {
        service.deleteMemberFromBoardById(id, user_id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
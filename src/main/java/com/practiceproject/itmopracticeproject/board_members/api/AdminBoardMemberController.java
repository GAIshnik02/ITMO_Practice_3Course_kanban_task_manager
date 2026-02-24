package com.practiceproject.itmopracticeproject.board_members.api;

import com.practiceproject.itmopracticeproject.board_members.dto.BoardMemberResponseDto;
import com.practiceproject.itmopracticeproject.board_members.dto.CreateBoardMemberRequest;
import com.practiceproject.itmopracticeproject.security.CurrentUser;
import com.practiceproject.itmopracticeproject.user.db.GlobalRole;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/boards/")
public class AdminBoardMemberController {

    private final BoardMemberService service;

    public AdminBoardMemberController(BoardMemberService service) {
        this.service = service;
    }

    @PostMapping("/{board_id}/members")
    public ResponseEntity<?> addMember(
            @Valid @RequestBody CreateBoardMemberRequest request,
            @PathVariable("board_id") Long board_id,
            @CurrentUser UserEntity user
    ) {
        checkRole(user.getRole());
        if (!user.getRole().equals(GlobalRole.ADMIN)) {
            throw new SecurityException("Access denied");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(service.addMember(board_id, request, user));
    }

    @GetMapping("/{board_id}/members")
    public ResponseEntity<?> getMembers(
            @PathVariable("board_id") Long board_id,
            @CurrentUser UserEntity user
    ) {
        checkRole(user.getRole());
        if (!user.getRole().equals(GlobalRole.ADMIN)) {
            throw new SecurityException("Access denied");
        }

        return ResponseEntity.status(HttpStatus.OK).body(service.getMembers(board_id, user));
    }

    @PutMapping("/{board_id}/members")
    public ResponseEntity<?> updateMember(
            @PathVariable("board_id") Long board_id,
            @Valid @RequestBody CreateBoardMemberRequest request,
            @CurrentUser UserEntity user
    ) {
        checkRole(user.getRole());
        if (!user.getRole().equals(GlobalRole.ADMIN)) {
            throw new SecurityException("Access denied");
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.updateMember(board_id, request, user));
    }

    @DeleteMapping("/{board_id}/members/{user_id}")
    public ResponseEntity<?> deleteMember(
            @PathVariable("board_id") Long board_id,
            @PathVariable("user_id") Long user_id,
            @CurrentUser UserEntity user
    ) {
        checkRole(user.getRole());
        if (!user.getRole().equals(GlobalRole.ADMIN)) {
            throw new SecurityException("Access denied");
        }

        service.deleteMemberFromBoardById(board_id, user_id, user);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    private void checkRole(GlobalRole role) {
        if (role != GlobalRole.ADMIN) {
            throw new SecurityException("Access denied");
        }
    }
}
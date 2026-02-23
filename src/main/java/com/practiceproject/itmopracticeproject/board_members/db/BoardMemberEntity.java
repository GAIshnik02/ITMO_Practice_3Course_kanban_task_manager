package com.practiceproject.itmopracticeproject.board_members.db;

import com.practiceproject.itmopracticeproject.board_members.domain.Role;
import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "boards_members")
public class BoardMemberEntity {

    @EmbeddedId
    private BoardMemberId id;

    @ManyToOne
    @MapsId("boardId")
    @JoinColumn(name = "board_id")
    private BoardEntity board;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "joined_at")
    private LocalDateTime joined_at;

    @Column(name = "left_at")
    private LocalDateTime left_at;

    @PrePersist
    public void onCreate() {
        this.joined_at = LocalDateTime.now();
    }

    public void leave() {
        this.left_at = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.left_at == null;
    }

    public void rejoin() {
        this.left_at = null;
        this.joined_at = LocalDateTime.now();
    }

    public BoardMemberEntity() {}

    public BoardMemberEntity(BoardEntity board,
                             UserEntity user_id,
                             Role role,
                             LocalDateTime joined_at,
                             LocalDateTime left_at
    ) {
        this.id = new BoardMemberId(board.getId(), user_id.getId());
        this.board = board;
        this.user = user_id;
        this.role = role;
        this.joined_at = joined_at;
        this.left_at = left_at;
    }

    public BoardMemberId getId() {
        return id;
    }

    public void setId(BoardMemberId id) {
        this.id = id;
    }

    public BoardEntity getBoard() {
        return board;
    }

    public void setBoard(BoardEntity board) {
        this.board = board;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getJoined_at() {
        return joined_at;
    }

    public void setJoined_at(LocalDateTime joined_at) {
        this.joined_at = joined_at;
    }

    public LocalDateTime getLeft_at() {
        return left_at;
    }

    public void setLeft_at(LocalDateTime left_at) {
        this.left_at = left_at;
    }
}
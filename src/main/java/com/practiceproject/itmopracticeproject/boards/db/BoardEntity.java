package com.practiceproject.itmopracticeproject.boards.db;


import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberEntity;
import com.practiceproject.itmopracticeproject.task.db.TaskEntity;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Transactional
@Table(name = "boards")
public class BoardEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskEntity> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardMemberEntity> members = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @PrePersist
    public void onCreate() {
        this.created_at = LocalDateTime.now();
        this.updated_at = this.created_at;
    }

    @PreUpdate
    public void onUpdate() {
        this.updated_at = LocalDateTime.now();
    }

    public BoardEntity() {
    }

    public BoardEntity(
            Long id,
            String name,
            String description,
            UserEntity owner,
            LocalDateTime created_at,
            LocalDateTime updated_at
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public void addTask(TaskEntity task) {
        tasks.add(task);
        task.setBoard(this);
    }

    public void removeTask(TaskEntity task) {
        tasks.remove(task);
        task.setBoard(null);
    }

    public void addMember(BoardMemberEntity member) {
        members.add(member);
        member.setBoard(this);
    }

    public void removeMember(BoardMemberEntity member) {
        members.remove(member);
        member.setBoard(null);
    }

    public List<TaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskEntity> tasks) {
        this.tasks = tasks;
    }

    public List<BoardMemberEntity> getMembers() {
        return members;
    }

    public void setMembers(List<BoardMemberEntity> members) {
        this.members = members;
    }
}
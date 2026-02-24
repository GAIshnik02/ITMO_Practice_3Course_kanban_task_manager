package com.practiceproject.itmopracticeproject.user.db;

import com.practiceproject.itmopracticeproject.board_members.db.BoardMemberEntity;
import com.practiceproject.itmopracticeproject.boards.db.BoardEntity;
import com.practiceproject.itmopracticeproject.task.db.TaskEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "users")
@Entity
public class UserEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "pass_hash", nullable = false)
    private String pass_hash;

    @Column(name = "first_name")
    private String first_name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "patronymic")
    private String patronymic;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private GlobalRole role = GlobalRole.USER;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardEntity> ownedBoards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade =  CascadeType.ALL, orphanRemoval = true)
    private List<BoardMemberEntity> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskEntity> createdTasks = new ArrayList<>();

    @ManyToMany(mappedBy = "assignees")
    private Set<TaskEntity> assignedTasks = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column (name = "updated_at")
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

    public UserEntity() {
    }

    @PreRemove
    public void preRemove() {
        for (TaskEntity task : assignedTasks) {
            task.getAssignees().remove(this);
        }
        assignedTasks.clear();
    }


    public UserEntity(Long id,
                      String login,
                      String first_name,
                      String surname,
                      String patronymic
    ) {
        this.id = id;
        this.login = login;
        this.first_name = first_name;
        this.surname = surname;
        this.patronymic = patronymic;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPass_hash(String pass_hash) {
        this.pass_hash = pass_hash;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public void setRole(GlobalRole role) { this.role = role; }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPass_hash() {
        return pass_hash;
    }

    public String getFirst_name() {
        return first_name;
    }

    public GlobalRole getRole() {return role;}

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public List<BoardEntity> getOwnedBoards() {
        return ownedBoards;
    }

    public void setOwnedBoards(List<BoardEntity> ownedBoards) {
        this.ownedBoards = ownedBoards;
    }

    public List<BoardMemberEntity> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<BoardMemberEntity> memberships) {
        this.memberships = memberships;
    }

    public List<TaskEntity> getCreatedTasks() {
        return createdTasks;
    }

    public void setCreatedTasks(List<TaskEntity> createdTasks) {
        this.createdTasks = createdTasks;
    }

    public Set<TaskEntity> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(Set<TaskEntity> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }
}

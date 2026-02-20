package com.practiceproject.itmopracticeproject;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

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

    @Column(name = "first_name", nullable = true)
    private String first_name;

    @Column(name = "surname", nullable = true)
    private String surname;

    @Column(name = "patronymic", nullable = true)
    private String patronymic;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column (name = "updated_at")
    private LocalDateTime updated_at;

    public UserEntity() {
    }

    public UserEntity(Long id,
                      String login,
                      String pass_hash,
                      String first_name,
                      String surname,
                      String patronymic,
                      LocalDateTime created_at,
                      LocalDateTime updated_at
    ) {
        this.id = id;
        this.login = login;
        this.pass_hash = pass_hash;
        this.first_name = first_name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.created_at = created_at;
        this.updated_at = updated_at;
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

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

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
}

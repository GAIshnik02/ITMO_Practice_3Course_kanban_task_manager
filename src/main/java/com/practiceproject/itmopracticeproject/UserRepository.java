package com.practiceproject.itmopracticeproject;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<UserEntity, Long> {


    @Transactional()
    @Modifying
    @Query("""
        UPDATE UserEntity ue
        set ue.login = :login,
        ue.pass_hash = :pass_hash,
        ue.first_name = :first_name,
        ue.surname = :surname,
        ue.patronymic = :patronymic,
        ue.updated_at = :updated_at
        where ue.id = :id
    """)
    void updateUserById(
            @Param("id") Long id,
            @Param("login") String login,
            @Param("pass_hash")  String pass_hash,
            @Param("first_name") String first_name,
            @Param("surname")  String surname,
            @Param("patronymic") String patronymic,
            @Param("updated_at") LocalDateTime updated_at
            );


}

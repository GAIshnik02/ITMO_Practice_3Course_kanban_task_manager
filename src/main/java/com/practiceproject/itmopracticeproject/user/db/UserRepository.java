package com.practiceproject.itmopracticeproject.user.db;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {


    @Transactional()
    @Modifying
    @Query("""
        UPDATE UserEntity ue
        set 
        ue.first_name = :first_name,
        ue.surname = :surname,
        ue.patronymic = :patronymic
        where ue.id = :id
    """)
    UserEntity updateUserById(
            @Param("id") Long id,
            @Param("first_name") String first_name,
            @Param("surname")  String surname,
            @Param("patronymic") String patronymic
            );


    boolean existsByLogin(String login);

    Optional<UserEntity> findByLogin(String login);
}

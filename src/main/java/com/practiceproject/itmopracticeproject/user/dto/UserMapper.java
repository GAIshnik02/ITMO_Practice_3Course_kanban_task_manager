package com.practiceproject.itmopracticeproject.user.dto;

import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toDto(UserEntity userEntity) {
        return new UserResponseDto(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getFirst_name(),
                userEntity.getSurname(),
                userEntity.getPatronymic(),
                userEntity.getRole(),
                userEntity.getCreated_at(),
                userEntity.getUpdated_at()
        );
    }
}
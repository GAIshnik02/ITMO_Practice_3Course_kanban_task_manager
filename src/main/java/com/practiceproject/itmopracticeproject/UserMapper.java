package com.practiceproject.itmopracticeproject;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toUserDto(UserEntity userEntity) {
        return new UserDto(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getPass_hash(),
                userEntity.getFirst_name(),
                userEntity.getSurname(),
                userEntity.getPatronymic(),
                userEntity.getCreated_at(),
                userEntity.getUpdated_at()
        );
    }

    public UserEntity toUserEntity(UserDto userDto) {
        return new UserEntity(
                userDto.id(),
                userDto.login(),
                userDto.pass_hash(),
                userDto.first_name(),
                userDto.surname(),
                userDto.patronymic(),
                userDto.created_at(),
                userDto.updated_at()
        );
    }
}

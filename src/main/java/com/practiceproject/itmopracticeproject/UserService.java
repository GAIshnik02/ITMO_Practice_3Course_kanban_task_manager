package com.practiceproject.itmopracticeproject;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto createUser(UserDto request) {
        var userToCreate = userMapper.toUserEntity(request);
        userToCreate.setCreated_at(LocalDateTime.now()); // setting created_at time
        var savedUser = userRepository.save(userToCreate);


        return userMapper.toUserDto(savedUser);
    }

    public UserDto getUserById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id: " + userId + " not found")
                );
        return userMapper.toUserDto(userEntity);
    }

    public UserDto updateUser(
            Long userId,
            UserDto request) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new EntityNotFoundException("User with id: " + userId + " not found");
        }
        var userToUpdate = userMapper.toUserEntity(request);
        userToUpdate.setUpdated_at(LocalDateTime.now());
        userRepository.updateUserById(
                userId,
                userToUpdate.getLogin(),
                userToUpdate.getPass_hash(),
                userToUpdate.getFirst_name(),
                userToUpdate.getSurname(),
                userToUpdate.getPatronymic(),
                userToUpdate.getUpdated_at()
        );
        return userMapper.toUserDto(userToUpdate);
    }

    public int deleteUserById(Long userId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new EntityNotFoundException("User with id: " + userId + " not found");
        }
        userRepository.deleteById(userId);
        return 1;
        //TODO: пофиксить мб позже проблемсы будут с каскадным дилитом
    }
}

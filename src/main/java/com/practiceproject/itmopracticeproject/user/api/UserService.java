package com.practiceproject.itmopracticeproject.user.api;

import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import com.practiceproject.itmopracticeproject.user.db.UserRepository;
import com.practiceproject.itmopracticeproject.user.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto getUserById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id: " + userId + " not found")
        );
        return userMapper.toDto(userEntity);
    }

    public UserResponseDto updateUser(
            Long userId,
            UserUpdateRequestDto request
    ) {
        UserEntity existingUser = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id: " + userId + " not found"
                ));

        if (request.first_name() != null) existingUser.setFirst_name(request.first_name());
        if (request.surname() != null) existingUser.setSurname(request.surname());
        if (request.patronymic() != null) existingUser.setPatronymic(request.patronymic());

        UserEntity updatedUser = userRepository.updateUserById(
                userId,
                request.first_name(),
                request.surname(),
                request.patronymic()
        );
        return userMapper.toDto(updatedUser);
    }

    public void deleteUserById(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id: " + userId + " not found"));
        userRepository.deleteById(userId);
    }

    //TODO: сделать замену логина
//    public void changeLogin (Long userId, UserChangeLoginRequest) {
//
//    }

    public void changePassword(Long userId,
                               UserChangePasswordRequest request
    ) {
        UserEntity user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id: " + userId + " not found")
        );
        if (!passwordEncoder.matches(request.oldPassword(), user.getPass_hash())) {
            throw new SecurityException("Wrong password");
        }
        user.setPass_hash(passwordEncoder.encode(request.newPassword()));
    }
}
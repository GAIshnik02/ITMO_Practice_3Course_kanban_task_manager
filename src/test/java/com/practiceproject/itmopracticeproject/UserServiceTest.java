package com.practiceproject.itmopracticeproject;

import com.practiceproject.itmopracticeproject.user.api.UserService;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import com.practiceproject.itmopracticeproject.user.db.UserRepository;
import com.practiceproject.itmopracticeproject.user.dto.UserChangePasswordRequest;
import com.practiceproject.itmopracticeproject.user.dto.UserMapper;
import com.practiceproject.itmopracticeproject.user.dto.UserResponseDto;
import com.practiceproject.itmopracticeproject.user.dto.UserUpdateRequestDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    private UserEntity testUser;
    private UserResponseDto testResponse;
    private UserUpdateRequestDto updateRequest;
    private UserChangePasswordRequest changePasswordRequest;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setLogin("testuser");
        testUser.setFirst_name("Test");
        testUser.setSurname("User");
        testUser.setPatronymic("ovich");
        testUser.setPass_hash("encodedOldPassword");

        testResponse = new UserResponseDto(
                1L, "testuser",  "Test", "User", "ovich", null, null, null
        );

        updateRequest = new UserUpdateRequestDto(
                "Updated", "User", "Updatedovich"
        );

        changePasswordRequest = new UserChangePasswordRequest(
                "oldPass", "newPass"
        );
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testResponse);

        // Act
        UserResponseDto result = userService.getUserById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.login()).isEqualTo("testuser");
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id: 999 not found");
    }

    @Test
    void updateUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(1L);
        updatedUser.setLogin("testuser");
        updatedUser.setFirst_name("Updated");
        updatedUser.setSurname("User");
        updatedUser.setPatronymic("Updatedovich");

        when(userRepository.updateUserById(1L, "Updated", "User", "Updatedovich"))
                .thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(new UserResponseDto(
                1L, "testuser", "Updated", "User", "Updatedovich", null, null, null
        ));

        // Act
        UserResponseDto result = userService.updateUser(1L, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.first_name()).isEqualTo("Updated");
        assertThat(result.patronymic()).isEqualTo("Updatedovich");

        verify(userRepository).updateUserById(1L, "Updated", "User", "Updatedovich");
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(999L, updateRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id: 999 not found");

        verify(userRepository, never()).updateUserById(any(), any(), any(), any());
    }

    @Test
    void deleteUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUserById(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserById_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUserById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id: 999 not found");

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void changePassword_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPassword");

        // Act
        userService.changePassword(1L, changePasswordRequest);

        // Assert
        assertThat(testUser.getPass_hash()).isEqualTo("encodedNewPassword");
        verify(userRepository, never()).save(any()); // Так как setPass_hash меняет поле в объекте
    }

    @Test
    void changePassword_WrongOldPassword_ThrowsSecurityException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass", "encodedOldPassword")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.changePassword(1L, changePasswordRequest))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Wrong password");

        assertThat(testUser.getPass_hash()).isEqualTo("encodedOldPassword"); // Пароль не изменился
    }

    @Test
    void changePassword_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.changePassword(999L, changePasswordRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id: 999 not found");
    }
}
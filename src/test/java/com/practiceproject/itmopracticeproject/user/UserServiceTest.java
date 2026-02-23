//package com.practiceproject.itmopracticeproject.user;
//
//import com.practiceproject.itmopracticeproject.user.api.UserService;
//import com.practiceproject.itmopracticeproject.user.db.UserEntity;
//import com.practiceproject.itmopracticeproject.user.db.UserRepository;
//import com.practiceproject.itmopracticeproject.user.domain.UserDto;
//import com.practiceproject.itmopracticeproject.user.domain.UserMapper;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @InjectMocks
//    private UserService userService;
//
//    private UserEntity userEntity;
//    private UserDto userDto;
//
//    @BeforeEach
//    void setUp() {
//        userEntity = new UserEntity(
//                1L,
//                "testuser",
//                "hash123",
//                "Test",
//                "User",
//                "Testovich",
//                LocalDateTime.now(),
//                LocalDateTime.now()
//        );
//
//        userDto = new UserDto(
//                1L,
//                "testuser",
//                "hash123",
//                "Test",
//                "User",
//                "Testovich",
//                LocalDateTime.now(),
//                LocalDateTime.now()
//        );
//    }
//
//    @Test
//    void getUserById_ShouldReturnUserDto_WhenUserExists() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
//        when(userMapper.toUserDto(userEntity)).thenReturn(userDto);
//
//        UserDto result = userService.getUserById(1L);
//
//        assertNotNull(result);
//        assertEquals(1L, result.id());
//        assertEquals("testuser", result.login());
//        verify(userRepository).findById(1L);
//    }
//
//    @Test
//    void getUserById_ShouldThrowException_WhenUserNotFound() {
//        when(userRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(99L));
//        verify(userRepository).findById(99L);
//    }
//
//    @Test
//    void createUser_ShouldReturnUserDto_WhenLoginIsUnique() {
//        UserDto createDto = new UserDto(
//                null, "newuser", "password", "New", "User", null, null, null
//        );
//
//        UserEntity newEntity = new UserEntity(
//                null, "newuser", "password", "New", "User", null, null, null
//        );
//
//        UserEntity savedEntity = new UserEntity(
//                2L, "newuser", "password", "New", "User", null, LocalDateTime.now(), LocalDateTime.now()
//        );
//
//        UserDto savedDto = new UserDto(
//                2L, "newuser", "password", "New", "User", null, LocalDateTime.now(), LocalDateTime.now()
//        );
//
//        when(userRepository.existsByLogin("newuser")).thenReturn(false);
//        when(userMapper.toUserEntity(createDto)).thenReturn(newEntity);
//        when(userRepository.save(newEntity)).thenReturn(savedEntity);
//        when(userMapper.toUserDto(savedEntity)).thenReturn(savedDto);
//
//        UserDto result = userService.createUser(createDto);
//
//        assertNotNull(result);
//        assertEquals(2L, result.id());
//        assertEquals("newuser", result.login());
//        verify(userRepository).save(any(UserEntity.class));
//    }
//
//    @Test
//    void createUser_ShouldThrowException_WhenLoginExists() {
//        UserDto createDto = new UserDto(
//                null, "existing", "password", "New", "User", null, null, null
//        );
//
//        when(userRepository.existsByLogin("existing")).thenReturn(true);
//
//        assertThrows(IllegalArgumentException.class, () -> userService.createUser(createDto));
//        verify(userRepository, never()).save(any());
//    }
//
//    @Test
//    void updateUser_ShouldUpdateOnlyChangedFields() {
//        UserDto updateDto = new UserDto(
//                1L, "testuser", "newhash", "Updated", null, null, null, null
//        );
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
//        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
//        when(userMapper.toUserDto(any())).thenReturn(updateDto);
//
//        UserDto result = userService.updateUser(1L, updateDto);
//
//        assertNotNull(result);
//        assertEquals("newhash", result.pass_hash());
//        assertEquals("Updated", result.first_name());
//        assertEquals("Test", result.surname());  // не изменилось
//    }
//
//    @Test
//    void deleteUser_ShouldDelete_WhenUserExists() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
//        doNothing().when(userRepository).deleteById(1L);
//
//        int result = userService.deleteUserById(1L);
//
//        assertEquals(1, result);
//        verify(userRepository).deleteById(1L);
//    }
//
//    @Test
//    void deleteUser_ShouldThrow_WhenUserNotFound() {
//        when(userRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserById(99L));
//        verify(userRepository, never()).deleteById(any());
//    }
//}
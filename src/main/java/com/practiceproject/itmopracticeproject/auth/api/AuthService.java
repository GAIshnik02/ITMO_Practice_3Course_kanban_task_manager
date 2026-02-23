package com.practiceproject.itmopracticeproject.auth.api;

import com.practiceproject.itmopracticeproject.auth.dto.AuthResponse;
import com.practiceproject.itmopracticeproject.auth.dto.LoginRequest;
import com.practiceproject.itmopracticeproject.auth.dto.RegisterRequest;
import com.practiceproject.itmopracticeproject.security.JwtUtil;
import com.practiceproject.itmopracticeproject.user.db.GlobalRole;
import com.practiceproject.itmopracticeproject.user.db.UserEntity;
import com.practiceproject.itmopracticeproject.user.db.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.login())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with that login already exists");
        }

        UserEntity userToRegister = new UserEntity();
        userToRegister.setLogin(request.login());
        userToRegister.setPass_hash(passwordEncoder.encode(request.password()));
        userToRegister.setRole(GlobalRole.USER);
        userToRegister.setFirst_name(request.first_name());
        userToRegister.setSurname(request.surname());
        userToRegister.setPatronymic(request.patronymic());
        UserEntity createdUser = userRepository.save(userToRegister);

        String token = jwtUtil.generateToken(createdUser);

        return new AuthResponse(token, createdUser.getLogin(), createdUser.getId());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.login(), request.password())
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        UserEntity user = userRepository.findByLogin(request.login())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token, user.getLogin(), user.getId());
    }
}

//package com.practiceproject.itmopracticeproject.auth;
//
//import com.practiceproject.itmopracticeproject.UserRepository;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    public UserDetailsServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
//        User user = userRepository.findByLogin(login)
//                                  .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getLogin(),
//                user.getPassHash(),
//                new ArrayList<>() // пока без ролей на уровне Security
//        );
//    }
//}

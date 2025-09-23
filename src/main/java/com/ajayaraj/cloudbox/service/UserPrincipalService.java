package com.ajayaraj.cloudbox.service;

import com.ajayaraj.cloudbox.model.User;
import com.ajayaraj.cloudbox.model.UserPrincipal;
import com.ajayaraj.cloudbox.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserPrincipalService implements UserDetailsService {

    private UserRepository userRepository;

    public UserPrincipalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailId(username);

        if(user == null) {
            throw new UsernameNotFoundException("User with email id : " + username + " not found!");
        }

        return new UserPrincipal(user);
    }
}

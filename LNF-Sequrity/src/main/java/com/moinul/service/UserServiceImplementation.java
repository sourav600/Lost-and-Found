package com.moinul.service;

import com.moinul.config.JwtProvider;
import com.moinul.model.User;
import com.moinul.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation  implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Override
    public User getUserProfile(String jwt) {
        String phoneNo = JwtProvider.getPhoneNoFromJwtToken(jwt);
        return userRepository.findByPhoneNo(phoneNo);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

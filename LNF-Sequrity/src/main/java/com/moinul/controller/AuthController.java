package com.moinul.controller;

import com.moinul.config.JwtProvider;
import com.moinul.model.User;
import com.moinul.repository.UserRepository;
import com.moinul.request.LoginRequest;
import com.moinul.response.AuthResponse;
import com.moinul.service.CustomUserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserServiceImplementation customUserDetails;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserhandler(
            @RequestBody User user) throws Exception{

        String phoneNo = user.getPhoneNo();
        String password = user.getPassword();
        String fullName = user.getFullName();

        User isPhoneNoExist = userRepository.findByPhoneNo(phoneNo);
        if(isPhoneNoExist!=null){
            throw new Exception("Phone No. is already used!");
        }

        User createdUser = new User();
        createdUser.setPhoneNo(phoneNo);
        createdUser.setFullName(fullName);
        createdUser.setPassword(passwordEncoder.encode(password));

        User savedUser = userRepository.save(createdUser);
        userRepository.save(savedUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(phoneNo, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Register Success!");
        authResponse.setStatus(true);

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest){
        String username = loginRequest.getPhoneNo();
        String password = loginRequest.getPassword();

        System.out.println(username+ "--------- "+ password);

        Authentication authentication = authenticate(username, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();

        authResponse.setMessage("Login Success");
        authResponse.setJwt(token);
        authResponse.setStatus(true);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        System.out.println("Sign in user Details: "+ userDetails);

        if(userDetails==null){
            System.out.println("Sign in user Details - null : "+userDetails);
            throw new BadCredentialsException("Invalid username or Password");
        }

        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            System.out.println("Sign in user Details - password not match : "+ userDetails);
            throw new BadCredentialsException("Invalid username or Password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}

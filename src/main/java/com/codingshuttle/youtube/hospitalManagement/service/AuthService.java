package com.codingshuttle.youtube.hospitalManagement.service;

import com.codingshuttle.youtube.hospitalManagement.dto.LoginRequestDto;
import com.codingshuttle.youtube.hospitalManagement.dto.LoginResponseDto;
import com.codingshuttle.youtube.hospitalManagement.dto.SignUpResponseDto;
import com.codingshuttle.youtube.hospitalManagement.entity.AppUser;
import com.codingshuttle.youtube.hospitalManagement.dto.SignUpRequestDto;
import com.codingshuttle.youtube.hospitalManagement.entity.type.AuthProviderType;
import com.codingshuttle.youtube.hospitalManagement.repository.UserRepository;
import com.codingshuttle.youtube.hospitalManagement.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername()
                        ,loginRequestDto.getPassword())
        );
        AppUser user = (AppUser) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);
        return new LoginResponseDto(token,user.getId());
    }

//    public AppUser signupUser(LoginRequestDto loginRequestDto) {
//        AppUser user = userRepository.findByUsername(loginRequestDto.getUsername()).orElse(null);
//        if(user!=null){
//            throw  new IllegalArgumentException("user already exists");
//        }
//        return userRepository.save(AppUser.builder()
//                .username(loginRequestDto.getUsername())
//                .password(passwordEncoder.encode(loginRequestDto.getPassword()))
//                .build());
//    }

    public SignUpResponseDto signup(SignUpRequestDto signUpRequestDto) {
        AppUser user = jwtService.signupUser(signUpRequestDto,null,AuthProviderType.EMAIL);
        userRepository.save(user);
        return modelMapper.map(user,SignUpResponseDto.class);
    }
}


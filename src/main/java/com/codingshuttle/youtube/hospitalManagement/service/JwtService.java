package com.codingshuttle.youtube.hospitalManagement.service;

import com.codingshuttle.youtube.hospitalManagement.dto.LoginResponseDto;
import com.codingshuttle.youtube.hospitalManagement.entity.AppUser;
import com.codingshuttle.youtube.hospitalManagement.entity.Patient;
import com.codingshuttle.youtube.hospitalManagement.dto.SignUpRequestDto;
import com.codingshuttle.youtube.hospitalManagement.entity.type.AuthProviderType;
import com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType;
import com.codingshuttle.youtube.hospitalManagement.repository.PatientRepository;
import com.codingshuttle.youtube.hospitalManagement.repository.UserRepository;
import com.codingshuttle.youtube.hospitalManagement.security.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;

    @Transactional
    public ResponseEntity<LoginResponseDto> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        AuthProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registrationId);
        String providerId = authUtil.getProviderId(registrationId, oAuth2User);
        AppUser user = userRepository.findByProviderIdAndProviderType(providerId, providerType).orElse(null);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if(name == null) {
            name = oAuth2User.getAttribute("login");
        }

        if(name == null) {
            name = "User_" + UUID.randomUUID();
        }

        System.out.println(email);
        AppUser emailUser = userRepository.findByUsername(email).orElse(null);

        if (user == null && emailUser == null) {
            //signup flow
            String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
            user = signupUser(new SignUpRequestDto
                    (username, null,name,new HashSet<>(Set.of(RoleType.PATIENT)))
                    ,providerId,providerType);

        } else if (user != null) {
            if (email != null && !email.isBlank() && !email.equals(user.getUsername())) {
                user.setUsername(email);
                userRepository.save(user);
            }
        } else {
            throw new BadCredentialsException("This email is already registered with provider " +
                    emailUser.getProviderType());
        }
        userRepository.saveAndFlush(user);
        LoginResponseDto loginResponseDto = new LoginResponseDto(authUtil.generateAccessToken(user), user.getId());
        return ResponseEntity.ok(loginResponseDto);
    }

    public AppUser signupUser(SignUpRequestDto signUpRequestDto, String providerId, AuthProviderType providerType) {
        AppUser user = userRepository.findByUsername(signUpRequestDto.getUsername()).orElse(null);
        if(user!=null){
            throw new BadCredentialsException("User already exists.");
        }
        user = AppUser.builder()
                .username(signUpRequestDto.getUsername())
                .providerId(providerId)
                .providerType(providerType)
                .roles(signUpRequestDto.getRoles())//for understanding
                .build();
        if(providerType==AuthProviderType.EMAIL){
            user.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        }
        userRepository.save(user);
        Patient patient = Patient.builder()
                .name(signUpRequestDto.getName())
                .email(signUpRequestDto.getUsername())
                .user(user)
                .build();
        patientRepository.save(patient);
        return user;
    }
}

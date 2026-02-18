package com.codingshuttle.youtube.hospitalManagement.service;

import com.codingshuttle.youtube.hospitalManagement.dto.DoctorResponseDto;
import com.codingshuttle.youtube.hospitalManagement.entity.AppUser;
import com.codingshuttle.youtube.hospitalManagement.entity.Doctor;
import com.codingshuttle.youtube.hospitalManagement.dto.DoctorRequestDto;
import com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType;
import com.codingshuttle.youtube.hospitalManagement.repository.DoctorRepository;
import com.codingshuttle.youtube.hospitalManagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public List<DoctorResponseDto> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctor -> modelMapper.map(doctor, DoctorResponseDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public DoctorResponseDto onBoardNewDoctor(DoctorRequestDto doctorRequestDto) {
        AppUser user = userRepository.findById(doctorRequestDto.getId()).orElseThrow();
        if(doctorRepository.findById(user.getId()).orElse(null)!=null){
            throw new BadCredentialsException("Doctor already exists with id : "
                    +doctorRequestDto.getId().toString());
          }
        Doctor doctor = Doctor.builder()
                .name(doctorRequestDto.getName())
                .email(doctorRequestDto.getEmail())
                .build();
        doctor.setUser(user);
        user.setRoles(Set.of(RoleType.DOCTOR));
        System.out.println(doctor);
        doctorRepository.save(doctor);
        return modelMapper.map(doctor, DoctorResponseDto.class);
    }
}

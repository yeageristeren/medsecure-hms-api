package com.medsecure.appointment.dto;

import com.medsecure.doctor.dto.DoctorResponseDto;
import com.medsecure.patient.dto.PatientResponseDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class AppointmentResponseDto {
    private Long id;
    private LocalDateTime appointmentTime;
    private String reason;
    private DoctorResponseDto doctor;
    private PatientResponseDto patient;
}

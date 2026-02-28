package com.medsecure.admin;

import com.medsecure.appointment.AppointmentRepository;
import com.medsecure.common.dto.SystemStatsDto;
import com.medsecure.doctor.DoctorRepository;
import com.medsecure.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DashboardService {


    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public SystemStatsDto getSystemStats() {

        Long totalDoctors = doctorRepository.count();
        Long totalPatients = patientRepository.count();
        Long totalAppointments = appointmentRepository.count();

        LocalDate today = LocalDate.now();
        //today appointments
        Long todayAppointments = appointmentRepository.countAppointmentByAppointmentTimeBetween(
                today.atStartOfDay(),today.atTime(LocalTime.MAX)
        );
        //weekly appointments
        Long weeklyAppointments = appointmentRepository.countAppointmentByAppointmentTimeBetween(
                today.with(DayOfWeek.MONDAY).atStartOfDay()
                ,today.with(DayOfWeek.FRIDAY).atTime(LocalTime.MAX)
        );
        //monthly appointments
        Long monthlyAppointments = appointmentRepository.countAppointmentByAppointmentTimeBetween(
                today.withDayOfMonth(1).atStartOfDay()
                ,today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX)
        );

        return new SystemStatsDto(totalDoctors,totalPatients,totalAppointments
                ,todayAppointments,weeklyAppointments,monthlyAppointments);
    }
}

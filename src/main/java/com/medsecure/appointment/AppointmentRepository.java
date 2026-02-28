package com.medsecure.appointment;

import com.medsecure.common.type.AppointmentStatus;
import com.medsecure.doctor.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    void removeAppointmentById(Long id);

    Page<Appointment> getAppointmentsByDoctor_Id(Long doctorId, Pageable pageable);

    boolean existsByAppointmentTimeAndDoctorAndStatusIn(LocalDateTime appointmentTime, Doctor doctor, List<AppointmentStatus> pending);
}
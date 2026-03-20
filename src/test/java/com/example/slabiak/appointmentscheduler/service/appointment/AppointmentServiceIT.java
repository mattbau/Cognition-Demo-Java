package com.example.slabiak.appointmentscheduler.service.appointment;

import com.example.slabiak.appointmentscheduler.entity.Appointment;
import com.example.slabiak.appointmentscheduler.entity.AppointmentStatus;
import com.example.slabiak.appointmentscheduler.service.AppointmentService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration-test")
public class AppointmentServiceIT {

    @Autowired
    private AppointmentService appointmentService;

    @Test
    @Transactional
    @WithUserDetails("admin")
    public void shouldSaveNewRetailCustomer() {
        appointmentService.createNewAppointment(1, 2, 3, LocalDateTime.of(2020, 02, 9, 12, 0, 0));

        List<Appointment> appointmentByProviderId = appointmentService.getAllAppointments();
        assertThat(appointmentByProviderId).hasSize(2);
        assertEquals(AppointmentStatus.SCHEDULED, appointmentByProviderId.get(0).getStatus());

    }

}

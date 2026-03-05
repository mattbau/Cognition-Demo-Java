package com.example.slabiak.appointmentscheduler.controller;

import com.example.slabiak.appointmentscheduler.dao.AppointmentRepository;
import com.example.slabiak.appointmentscheduler.dao.user.UserRepository;
import com.example.slabiak.appointmentscheduler.entity.Appointment;
import com.example.slabiak.appointmentscheduler.entity.user.User;
import com.example.slabiak.appointmentscheduler.model.AppointmentDetailDTO;
import com.example.slabiak.appointmentscheduler.model.UserDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agent-api")
public class AgentApiController {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public AgentApiController(UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<UserDTO> getUserByUsername(@RequestParam("username") String username) {
        Optional<User> userOpt = userRepository.findByUserName(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserDTO.fromUser(userOpt.get()));
    }

    @GetMapping("/users/{userId}/appointments")
    public ResponseEntity<List<AppointmentDetailDTO>> getUserAppointments(
            @PathVariable("userId") int userId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        List<Appointment> appointments;

        boolean isProvider = user.hasRole("ROLE_PROVIDER");

        if (from != null && to != null) {
            LocalDateTime fromDateTime = from.atStartOfDay();
            LocalDateTime toDateTime = to.atTime(LocalTime.MAX);
            if (isProvider) {
                appointments = appointmentRepository.findByProviderIdAndStartBetween(userId, fromDateTime, toDateTime);
            } else {
                appointments = appointmentRepository.findByCustomerIdAndStartBetween(userId, fromDateTime, toDateTime);
            }
        } else if (from != null || to != null) {
            return ResponseEntity.badRequest().build();
        } else {
            if (isProvider) {
                appointments = appointmentRepository.findByProviderId(userId);
            } else {
                appointments = appointmentRepository.findByCustomerId(userId);
            }
        }

        if (status != null && !status.isEmpty()) {
            appointments = appointments.stream()
                    .filter(a -> a.getStatus() != null && a.getStatus().toString().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        List<AppointmentDetailDTO> dtos = appointments.stream()
                .map(AppointmentDetailDTO::fromAppointment)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<AppointmentDetailDTO> getAppointmentById(@PathVariable("id") int id) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (!appointmentOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AppointmentDetailDTO.fromAppointment(appointmentOpt.get()));
    }
}

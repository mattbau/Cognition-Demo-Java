package com.example.slabiak.appointmentscheduler.controller;

import com.example.slabiak.appointmentscheduler.dao.AppointmentRepository;
import com.example.slabiak.appointmentscheduler.dao.user.UserRepository;
import com.example.slabiak.appointmentscheduler.entity.Appointment;
import com.example.slabiak.appointmentscheduler.entity.AppointmentStatus;
import com.example.slabiak.appointmentscheduler.entity.Work;
import com.example.slabiak.appointmentscheduler.entity.user.Role;
import com.example.slabiak.appointmentscheduler.entity.user.User;
import com.example.slabiak.appointmentscheduler.entity.user.customer.RetailCustomer;
import com.example.slabiak.appointmentscheduler.entity.user.provider.Provider;
import com.example.slabiak.appointmentscheduler.model.AppointmentDetailDTO;
import com.example.slabiak.appointmentscheduler.model.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AgentApiControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AgentApiController controller;

    private Provider provider;
    private RetailCustomer customer;
    private Work work;
    private Appointment appointment;

    @Before
    public void setUp() {
        provider = new Provider();
        provider.setId(1);
        provider.setFirstName("Dr.");
        provider.setLastName("Smith");
        provider.setUserName("drsmith");
        provider.setEmail("drsmith@example.com");
        Role providerRole = new Role("ROLE_PROVIDER");
        provider.setRoles(Arrays.asList(providerRole));

        customer = new RetailCustomer();
        customer.setId(2);
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer.setUserName("janedoe");
        customer.setEmail("jane@example.com");
        Role customerRole = new Role("ROLE_CUSTOMER");
        Role retailRole = new Role("ROLE_CUSTOMER_RETAIL");
        customer.setRoles(Arrays.asList(customerRole, retailRole));

        work = new Work();
        work.setName("Checkup");
        work.setDuration(30);
        work.setPrice(50.0);

        appointment = new Appointment();
        appointment.setId(100);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setStart(LocalDateTime.of(2025, 7, 1, 9, 0));
        appointment.setEnd(LocalDateTime.of(2025, 7, 1, 9, 30));
        appointment.setProvider(provider);
        appointment.setCustomer(customer);
        appointment.setWork(work);
    }

    // --- GET /agent-api/users ---

    @Test
    public void getUserByUsername_shouldReturnUserWhenFound() {
        when(userRepository.findByUserName("janedoe")).thenReturn(Optional.of(customer));

        ResponseEntity<UserDTO> response = controller.getUserByUsername("janedoe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("janedoe", response.getBody().getUserName());
        assertEquals("Jane", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
    }

    @Test
    public void getUserByUsername_shouldReturn404WhenNotFound() {
        when(userRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        ResponseEntity<UserDTO> response = controller.getUserByUsername("unknown");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // --- GET /agent-api/users/{userId}/appointments ---

    @Test
    public void getUserAppointments_shouldReturnAppointmentsForCustomer() {
        when(userRepository.findById(2)).thenReturn(Optional.of(customer));
        when(appointmentRepository.findByCustomerId(2)).thenReturn(Arrays.asList(appointment));

        ResponseEntity<?> response = controller.getUserAppointments(2, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        AppointmentDetailDTO dto = (AppointmentDetailDTO) body.get(0);
        assertEquals(100, dto.getId());
        assertEquals("SCHEDULED", dto.getStatus());
    }

    @Test
    public void getUserAppointments_shouldReturnAppointmentsForProvider() {
        when(userRepository.findById(1)).thenReturn(Optional.of(provider));
        when(appointmentRepository.findByProviderId(1)).thenReturn(Arrays.asList(appointment));

        ResponseEntity<?> response = controller.getUserAppointments(1, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
    }

    @Test
    public void getUserAppointments_shouldReturn404ForUnknownUser() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getUserAppointments(999, null, null, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getUserAppointments_shouldFilterByStatus() {
        Appointment canceledAppointment = new Appointment();
        canceledAppointment.setId(101);
        canceledAppointment.setStatus(AppointmentStatus.CANCELED);
        canceledAppointment.setStart(LocalDateTime.of(2025, 7, 2, 10, 0));
        canceledAppointment.setEnd(LocalDateTime.of(2025, 7, 2, 10, 30));
        canceledAppointment.setProvider(provider);
        canceledAppointment.setCustomer(customer);
        canceledAppointment.setWork(work);

        when(userRepository.findById(2)).thenReturn(Optional.of(customer));
        when(appointmentRepository.findByCustomerId(2)).thenReturn(Arrays.asList(appointment, canceledAppointment));

        ResponseEntity<?> response = controller.getUserAppointments(2, "SCHEDULED", null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals("SCHEDULED", ((AppointmentDetailDTO) body.get(0)).getStatus());
    }

    @Test
    public void getUserAppointments_shouldFilterByDateRange() {
        LocalDate from = LocalDate.of(2025, 7, 1);
        LocalDate to = LocalDate.of(2025, 7, 31);

        when(userRepository.findById(2)).thenReturn(Optional.of(customer));
        when(appointmentRepository.findByCustomerIdAndStartBetween(
                eq(2),
                eq(from.atStartOfDay()),
                eq(to.atTime(LocalTime.MAX))
        )).thenReturn(Arrays.asList(appointment));

        ResponseEntity<?> response = controller.getUserAppointments(2, null, from, to);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        verify(appointmentRepository).findByCustomerIdAndStartBetween(eq(2), any(), any());
    }

    @Test
    public void getUserAppointments_shouldReturnBadRequestForPartialDateRange() {
        when(userRepository.findById(2)).thenReturn(Optional.of(customer));

        ResponseEntity<?> response = controller.getUserAppointments(2, null, LocalDate.of(2025, 1, 1), null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void getUserAppointments_shouldReturnBadRequestForOnlyToDate() {
        when(userRepository.findById(2)).thenReturn(Optional.of(customer));

        ResponseEntity<?> response = controller.getUserAppointments(2, null, null, LocalDate.of(2025, 12, 31));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void getUserAppointments_shouldUseDateRangeForProvider() {
        LocalDate from = LocalDate.of(2025, 7, 1);
        LocalDate to = LocalDate.of(2025, 7, 31);

        when(userRepository.findById(1)).thenReturn(Optional.of(provider));
        when(appointmentRepository.findByProviderIdAndStartBetween(
                eq(1),
                eq(from.atStartOfDay()),
                eq(to.atTime(LocalTime.MAX))
        )).thenReturn(Arrays.asList(appointment));

        ResponseEntity<?> response = controller.getUserAppointments(1, null, from, to);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(appointmentRepository).findByProviderIdAndStartBetween(eq(1), any(), any());
    }

    @Test
    public void getUserAppointments_shouldReturnEmptyListWhenNoAppointments() {
        when(userRepository.findById(2)).thenReturn(Optional.of(customer));
        when(appointmentRepository.findByCustomerId(2)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = controller.getUserAppointments(2, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        assertNotNull(body);
        assertTrue(body.isEmpty());
    }

    // --- GET /agent-api/appointments/{id} ---

    @Test
    public void getAppointmentById_shouldReturnAppointmentWhenFound() {
        when(appointmentRepository.findById(100)).thenReturn(Optional.of(appointment));

        ResponseEntity<AppointmentDetailDTO> response = controller.getAppointmentById(100);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100, response.getBody().getId());
        assertEquals("Dr. Smith", response.getBody().getProviderName());
        assertEquals("Jane Doe", response.getBody().getCustomerName());
        assertEquals("Checkup", response.getBody().getWorkName());
    }

    @Test
    public void getAppointmentById_shouldReturn404WhenNotFound() {
        when(appointmentRepository.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<AppointmentDetailDTO> response = controller.getAppointmentById(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}

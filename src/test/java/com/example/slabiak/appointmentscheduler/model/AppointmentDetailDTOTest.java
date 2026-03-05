package com.example.slabiak.appointmentscheduler.model;

import com.example.slabiak.appointmentscheduler.entity.Appointment;
import com.example.slabiak.appointmentscheduler.entity.AppointmentStatus;
import com.example.slabiak.appointmentscheduler.entity.Invoice;
import com.example.slabiak.appointmentscheduler.entity.Work;
import com.example.slabiak.appointmentscheduler.entity.user.User;
import com.example.slabiak.appointmentscheduler.entity.user.customer.RetailCustomer;
import com.example.slabiak.appointmentscheduler.entity.user.provider.Provider;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class AppointmentDetailDTOTest {

    private Appointment appointment;
    private Provider provider;
    private RetailCustomer customer;
    private Work work;

    @Before
    public void setUp() {
        provider = new Provider();
        provider.setId(10);
        provider.setFirstName("John");
        provider.setLastName("Doe");

        customer = new RetailCustomer();
        customer.setId(20);
        customer.setFirstName("Jane");
        customer.setLastName("Smith");

        work = new Work();
        work.setName("Consultation");
        work.setDuration(60);
        work.setPrice(99.99);

        appointment = new Appointment();
        appointment.setId(1);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setStart(LocalDateTime.of(2025, 6, 15, 10, 0));
        appointment.setEnd(LocalDateTime.of(2025, 6, 15, 11, 0));
        appointment.setProvider(provider);
        appointment.setCustomer(customer);
        appointment.setWork(work);
    }

    @Test
    public void shouldMapAllFieldsFromAppointment() {
        AppointmentDetailDTO dto = AppointmentDetailDTO.fromAppointment(appointment);

        assertEquals(1, dto.getId());
        assertEquals("SCHEDULED", dto.getStatus());
        assertEquals(LocalDateTime.of(2025, 6, 15, 10, 0), dto.getStart());
        assertEquals(LocalDateTime.of(2025, 6, 15, 11, 0), dto.getEnd());
        assertEquals("John Doe", dto.getProviderName());
        assertEquals(10, dto.getProviderId());
        assertEquals("Jane Smith", dto.getCustomerName());
        assertEquals(20, dto.getCustomerId());
        assertEquals("Consultation", dto.getWorkName());
        assertEquals(60, dto.getWorkDuration());
        assertEquals(99.99, dto.getWorkPrice(), 0.001);
    }

    @Test
    public void shouldHandleNullInvoice() {
        AppointmentDetailDTO dto = AppointmentDetailDTO.fromAppointment(appointment);

        assertNull(dto.getInvoiceNumber());
    }

    @Test
    public void shouldMapInvoiceNumber() {
        Invoice invoice = new Invoice();
        invoice.setNumber("INV-001");
        appointment.setInvoice(invoice);

        AppointmentDetailDTO dto = AppointmentDetailDTO.fromAppointment(appointment);

        assertEquals("INV-001", dto.getInvoiceNumber());
    }

    @Test
    public void shouldHandleNullCanceler() {
        AppointmentDetailDTO dto = AppointmentDetailDTO.fromAppointment(appointment);

        assertNull(dto.getCanceledAt());
        assertNull(dto.getCancelerName());
    }

    @Test
    public void shouldMapCancellationFields() {
        User canceler = new User();
        canceler.setFirstName("Admin");
        canceler.setLastName("User");
        appointment.setCanceler(canceler);
        appointment.setCanceledAt(LocalDateTime.of(2025, 6, 14, 9, 0));
        appointment.setStatus(AppointmentStatus.CANCELED);

        AppointmentDetailDTO dto = AppointmentDetailDTO.fromAppointment(appointment);

        assertEquals("Admin User", dto.getCancelerName());
        assertEquals(LocalDateTime.of(2025, 6, 14, 9, 0), dto.getCanceledAt());
        assertEquals("CANCELED", dto.getStatus());
    }

    @Test
    public void shouldHandleNullStatus() {
        appointment.setStatus(null);

        AppointmentDetailDTO dto = AppointmentDetailDTO.fromAppointment(appointment);

        assertNull(dto.getStatus());
    }
}

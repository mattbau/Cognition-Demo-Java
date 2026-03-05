package com.example.slabiak.appointmentscheduler.model;

import com.example.slabiak.appointmentscheduler.entity.Appointment;

import java.time.LocalDateTime;

public class AppointmentDetailDTO {

    private int id;
    private String status;
    private LocalDateTime start;
    private LocalDateTime end;
    private String providerName;
    private int providerId;
    private String customerName;
    private int customerId;
    private String workName;
    private int workDuration;
    private double workPrice;
    private String invoiceNumber;
    private LocalDateTime canceledAt;
    private String cancelerName;

    public static AppointmentDetailDTO fromAppointment(Appointment a) {
        AppointmentDetailDTO dto = new AppointmentDetailDTO();
        dto.id = a.getId();
        dto.status = a.getStatus() != null ? a.getStatus().toString() : null;
        dto.start = a.getStart();
        dto.end = a.getEnd();
        dto.providerName = a.getProvider().getFirstName() + " " + a.getProvider().getLastName();
        dto.providerId = a.getProvider().getId();
        dto.customerName = a.getCustomer().getFirstName() + " " + a.getCustomer().getLastName();
        dto.customerId = a.getCustomer().getId();
        dto.workName = a.getWork().getName();
        dto.workDuration = a.getWork().getDuration();
        dto.workPrice = a.getWork().getPrice();
        dto.invoiceNumber = a.getInvoice() != null ? a.getInvoice().getNumber() : null;
        dto.canceledAt = a.getCanceledAt();
        dto.cancelerName = a.getCanceler() != null
                ? a.getCanceler().getFirstName() + " " + a.getCanceler().getLastName()
                : null;
        return dto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public int getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(int workDuration) {
        this.workDuration = workDuration;
    }

    public double getWorkPrice() {
        return workPrice;
    }

    public void setWorkPrice(double workPrice) {
        this.workPrice = workPrice;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    public String getCancelerName() {
        return cancelerName;
    }

    public void setCancelerName(String cancelerName) {
        this.cancelerName = cancelerName;
    }
}

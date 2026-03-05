package com.example.slabiak.appointmentscheduler.model;

import com.example.slabiak.appointmentscheduler.entity.user.Role;
import com.example.slabiak.appointmentscheduler.entity.user.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class UserDTOTest {

    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setId(1);
        user.setUserName("jdoe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");

        Role adminRole = new Role("ROLE_ADMIN");
        Role providerRole = new Role("ROLE_PROVIDER");
        Collection<Role> roles = Arrays.asList(adminRole, providerRole);
        user.setRoles(roles);
    }

    @Test
    public void shouldMapAllFieldsFromUser() {
        UserDTO dto = UserDTO.fromUser(user);

        assertEquals(1, dto.getId());
        assertEquals("jdoe", dto.getUserName());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    public void shouldMapRoleNames() {
        UserDTO dto = UserDTO.fromUser(user);

        assertEquals(2, dto.getRoles().size());
        assertTrue(dto.getRoles().contains("ROLE_ADMIN"));
        assertTrue(dto.getRoles().contains("ROLE_PROVIDER"));
    }

    @Test
    public void shouldHandleSingleRole() {
        Role customerRole = new Role("ROLE_CUSTOMER");
        Collection<Role> roles = Arrays.asList(customerRole);
        user.setRoles(roles);

        UserDTO dto = UserDTO.fromUser(user);

        assertEquals(1, dto.getRoles().size());
        assertTrue(dto.getRoles().contains("ROLE_CUSTOMER"));
    }
}

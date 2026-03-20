package com.example.slabiak.appointmentscheduler.validation;

import com.example.slabiak.appointmentscheduler.model.UserForm;
import com.example.slabiak.appointmentscheduler.validation.groups.UpdateProvider;
import com.example.slabiak.appointmentscheduler.validation.groups.UpdateUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateProviderValidationTest {

    private ValidatorFactory factory;
    private Validator validator;

    @BeforeEach
    public void stup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldHave9ViolationsForEmptyFormWhenUpdateProvider() {
        UserForm form = new UserForm();
        Set<ConstraintViolation<UserForm>> violations = validator.validate(form, UpdateUser.class, UpdateProvider.class);
        assertEquals(violations.size(), 9);
    }
}

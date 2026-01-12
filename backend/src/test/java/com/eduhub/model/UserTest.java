package com.eduhub.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertNotNull(user, "Default constructor should create a non-null User object");
    }

    @Test
    void testParameterizedConstructor() {
        User user = new User(1, "John", "Doe", "john@example.com", "password123", Role.STUDENT);
        
        assertNotNull(user, "Parameterized constructor should create a non-null User object");
        assertEquals(1, user.getId(), "ID should match");
        assertEquals("John", user.getFirstname(), "First name should match");
        assertEquals("Doe", user.getLastname(), "Last name should match");
        assertEquals("john@example.com", user.getEmail(), "Email should match");
        assertEquals("password123", user.getPassword(), "Password should match");
        assertEquals(Role.STUDENT, user.getRole(), "Role should match");
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("secure");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setRole(Role.PROFESSOR);

        assertEquals("test@example.com", user.getEmail());
        assertEquals("secure", user.getPassword());
        assertEquals("Test", user.getFirstname());
        assertEquals("User", user.getLastname());
        assertEquals(Role.PROFESSOR, user.getRole());
    }
}

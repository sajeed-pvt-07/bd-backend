package com.test.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HealthControllerTest {

    @Test
    void reportsApplicationAsUp() {
        assertEquals("UP", new HealthController().health().get("status"));
    }
}

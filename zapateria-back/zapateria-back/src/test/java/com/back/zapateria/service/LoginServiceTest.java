package com.back.zapateria.service;

import com.back.zapateria.dto.LoginRequest;
import com.back.zapateria.dto.LoginResponse;
import com.back.zapateria.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {

    private LoginService loginService;

    @BeforeEach
    void setUp() {
        // loginService = new LoginService(...);
    }

    // Los tests están comentados por ahora para evitar ejecución automática.
    // @Test
    // void authenticate_success() {
    //     LoginRequest req = new LoginRequest("user@example.com", "password");
    //     LoginResponse resp = loginService.authenticate(req);
    //     assertNotNull(resp);
    //     assertNotNull(resp.getToken());
    // }

    // @Test
    // void register_createsUser() {
    //     // Arrange register request
    //     // Call loginService.register(...)
    //     // Assert user saved and returned
    // }
}

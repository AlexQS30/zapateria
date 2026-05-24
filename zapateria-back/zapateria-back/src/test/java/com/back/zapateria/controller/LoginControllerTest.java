package com.back.zapateria.controller;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.back.zapateria.dto.LoginRequest;
import com.back.zapateria.dto.LoginResponse;
import com.back.zapateria.dto.RegisterRequest;
import com.back.zapateria.model.User;
import com.back.zapateria.service.LoginService;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController controller;

    @Test
    void login_success_returnsOk() {
        LoginRequest request = new LoginRequest("user@demo.com", "123456");
        LoginResponse response = new LoginResponse(1L, "user@demo.com", "Ana", "Perez", "USER", "token", "ok", true);
        when(loginService.authenticate(request)).thenReturn(response);

        ResponseEntity<LoginResponse> result = controller.login(request);

        assertEquals(200, result.getStatusCode().value());
        assertTrue(result.getBody().isSuccess());
        verify(loginService).authenticate(request);
    }

    @Test
    void login_failure_returnsUnauthorized() {
        LoginRequest request = new LoginRequest("user@demo.com", "123456");
        LoginResponse response = new LoginResponse(1L, "user@demo.com", "Ana", "Perez", "USER", "Credenciales inválidas", false);
        when(loginService.authenticate(request)).thenReturn(response);

        ResponseEntity<LoginResponse> result = controller.login(request);

        assertEquals(401, result.getStatusCode().value());
        assertFalse(result.getBody().isSuccess());
    }

    @Test
    void register_success_returnsCreated() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@demo.com");
        request.setPassword("123456");
        request.setFirstName("New");
        request.setLastName("User");

        LoginResponse response = new LoginResponse(2L, "new@demo.com", "New", "User", "USER", "Creado", true);
        when(loginService.register(request)).thenReturn(response);

        ResponseEntity<LoginResponse> result = controller.register(request);

        assertEquals(201, result.getStatusCode().value());
        assertTrue(result.getBody().isSuccess());
    }

    @Test
    void register_failure_returnsBadRequest() {
        RegisterRequest request = new RegisterRequest();
        LoginResponse response = new LoginResponse(null, null, null, null, null, "Email existente", false);
        when(loginService.register(request)).thenReturn(response);

        ResponseEntity<LoginResponse> result = controller.register(request);

        assertEquals(400, result.getStatusCode().value());
        assertFalse(result.getBody().isSuccess());
    }

    @Test
    void getUsers_returnsUsers() {
        User user = new User();
        user.setId(10L);
        user.setEmail("u@demo.com");
        when(loginService.getAllUsers()).thenReturn(List.of(user));

        ResponseEntity<List<User>> result = controller.getUsers();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void updateUser_successAndFailure_paths() {
        RegisterRequest request = new RegisterRequest();
        LoginResponse ok = new LoginResponse(1L, "u@demo.com", "A", "B", "USER", "Actualizado", true);
        LoginResponse bad = new LoginResponse(1L, "u@demo.com", "A", "B", "USER", "Error", false);

        when(loginService.updateUser(1L, request)).thenReturn(ok);
        ResponseEntity<LoginResponse> okResult = controller.updateUser(1L, request);
        assertEquals(200, okResult.getStatusCode().value());

        when(loginService.updateUser(2L, request)).thenReturn(bad);
        ResponseEntity<LoginResponse> badResult = controller.updateUser(2L, request);
        assertEquals(400, badResult.getStatusCode().value());
    }

    @Test
    void deleteUser_successAndFailure_paths() {
        LoginResponse ok = new LoginResponse(1L, "u@demo.com", "A", "B", "USER", "Eliminado", true);
        LoginResponse notFound = new LoginResponse(1L, "u@demo.com", "A", "B", "USER", "No encontrado", false);

        when(loginService.deleteUser(1L)).thenReturn(ok);
        ResponseEntity<LoginResponse> okResult = controller.deleteUser(1L);
        assertEquals(200, okResult.getStatusCode().value());

        when(loginService.deleteUser(2L)).thenReturn(notFound);
        ResponseEntity<LoginResponse> badResult = controller.deleteUser(2L);
        assertEquals(404, badResult.getStatusCode().value());
    }

    @Test
    void checkEmail_returnsServiceValue() {
        when(loginService.emailExists("exists@demo.com")).thenReturn(true);

        ResponseEntity<Boolean> result = controller.checkEmail("exists@demo.com");

        assertEquals(200, result.getStatusCode().value());
        assertTrue(result.getBody());
    }
}


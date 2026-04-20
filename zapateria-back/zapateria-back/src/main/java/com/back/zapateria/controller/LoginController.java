package com.back.zapateria.controller;

import com.back.zapateria.dto.LoginRequest;
import com.back.zapateria.dto.LoginResponse;
import com.back.zapateria.dto.RegisterRequest;
import com.back.zapateria.model.User;
import com.back.zapateria.service.LoginService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Autenticación", description = "Servicios para login, registro y administración de usuarios")
public class LoginController {

    @Autowired
    private LoginService loginService;

    // ================= LOGIN =================
    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario", description = "Valida credenciales y retorna la respuesta de login con el rol del usuario")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = loginService.authenticate(loginRequest);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta de usuario con rol por defecto USER")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        LoginResponse response = loginService.register(registerRequest);
        return response.isSuccess()
                ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ================= LIST USERS =================
    @GetMapping("/users")
    @Operation(summary = "Listar usuarios activos", description = "Retorna los usuarios activos del sistema")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(loginService.getAllUsers());
    }

    // ================= UPDATE USER =================
    @PutMapping("/users/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    public ResponseEntity<LoginResponse> updateUser(
            @PathVariable Long id,
            @RequestBody RegisterRequest request
    ) {
        LoginResponse response = loginService.updateUser(id, request);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ================= DELETE USER (SOFT) =================
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Eliminar usuario", description = "Marca el usuario como inactivo sin borrarlo físicamente")
    public ResponseEntity<LoginResponse> deleteUser(@PathVariable Long id) {
        LoginResponse response = loginService.deleteUser(id);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ================= CHECK EMAIL =================
    @GetMapping("/check-email/{email}")
    @Operation(summary = "Verificar email", description = "Indica si un email ya existe en el sistema")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
        return ResponseEntity.ok(loginService.emailExists(email));
    }
}
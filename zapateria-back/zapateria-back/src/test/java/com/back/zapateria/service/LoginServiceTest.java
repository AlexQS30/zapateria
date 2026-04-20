package com.back.zapateria.service;

import com.back.zapateria.dto.LoginRequest;
import com.back.zapateria.dto.LoginResponse;
import com.back.zapateria.dto.RegisterRequest;
import com.back.zapateria.model.User;
import com.back.zapateria.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {

    private LoginService loginService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private final List<User> store = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginService = new LoginService();

        try {
            Field userRepositoryField = LoginService.class.getDeclaredField("userRepository");
            userRepositoryField.setAccessible(true);
            userRepositoryField.set(loginService, userRepository);

            Field passwordEncoderField = LoginService.class.getDeclaredField("passwordEncoder");
            passwordEncoderField.setAccessible(true);
            passwordEncoderField.set(loginService, passwordEncoder);
        } catch (Exception ignored) {}

        User activeUser = buildUser(1L, "test@example.com", "encoded-password", true, "USER");
        store.add(activeUser);

        User inactiveUser = buildUser(2L, "inactive@example.com", "encoded-password", false, "USER");
        store.add(inactiveUser);

        when(userRepository.findByEmail(anyString())).thenAnswer(invocation -> {
            String email = invocation.getArgument(0);
            return store.stream().filter(user -> user.getEmail().equals(email)).findFirst();
        });
        when(userRepository.existsByEmail(anyString())).thenAnswer(invocation -> {
            String email = invocation.getArgument(0);
            return store.stream().anyMatch(user -> user.getEmail().equals(email));
        });
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return store.stream().filter(user -> user.getId().equals(id)).findFirst();
        });
        when(userRepository.findByIsActiveTrue()).thenAnswer(invocation -> store.stream().filter(user -> Boolean.TRUE.equals(user.getIsActive())).toList());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user.getId() == null) {
                user.setId((long) (store.size() + 1));
            }
            store.removeIf(existing -> existing.getId().equals(user.getId()));
            store.add(user);
            return user;
        });
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "encoded-" + invocation.getArgument(0));
        when(passwordEncoder.matches(anyString(), anyString())).thenAnswer(invocation -> {
            String raw = invocation.getArgument(0);
            String encoded = invocation.getArgument(1);
            return encoded.equals("encoded-" + raw) || encoded.equals("encoded-password");
        });
    }

    private User buildUser(Long id, String email, String password, Boolean active, String role) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setIsActive(active);
        user.setRole(role);
        return user;
    }

    // Este test valida que la autenticación funcione cuando el usuario existe, está activo y la contraseña coincide.
    @Test
    void authenticate_success() {
        LoginResponse resp = loginService.authenticate(new LoginRequest("test@example.com", "password123"));
        assertTrue(resp.isSuccess());
        assertEquals("Login exitoso", resp.getMessage());
        assertEquals("test@example.com", resp.getEmail());
    }

    // Este test comprueba que el servicio rechace un usuario que no existe en la base.
    @Test
    void authenticate_userNotFound() {
        LoginResponse resp = loginService.authenticate(new LoginRequest("missing@example.com", "password123"));
        assertFalse(resp.isSuccess());
        assertEquals("Usuario no encontrado", resp.getMessage());
    }

    // Este test valida que el servicio bloquee el acceso cuando la contraseña no coincide.
    @Test
    void authenticate_wrongPassword() {
        when(passwordEncoder.matches("bad-pass", "encoded-password")).thenReturn(false);
        LoginResponse resp = loginService.authenticate(new LoginRequest("test@example.com", "bad-pass"));
        assertFalse(resp.isSuccess());
        assertEquals("Contraseña incorrecta", resp.getMessage());
    }

    // Este test comprueba que un usuario inactivo no pueda iniciar sesión.
    @Test
    void authenticate_inactiveUser() {
        LoginResponse resp = loginService.authenticate(new LoginRequest("inactive@example.com", "password123"));
        assertFalse(resp.isSuccess());
        assertEquals("Usuario inactivo", resp.getMessage());
    }

    // Este test valida que el registro cree un nuevo usuario con rol USER y contraseña encriptada.
    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@example.com");
        req.setPassword("secret123");
        req.setFirstName("New");
        req.setLastName("User");
        req.setPhoneNumber("999999999");
        req.setAddress("Av. Lima");
        req.setCity("Lima");
        req.setPostalCode("15001");

        LoginResponse resp = loginService.register(req);
        assertTrue(resp.isSuccess());
        assertEquals("Usuario registrado", resp.getMessage());
        assertEquals("new@example.com", resp.getEmail());
    }

    // Este test comprueba que el registro no permita correos duplicados.
    @Test
    void register_emailAlreadyExists() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setPassword("secret123");
        req.setFirstName("New");
        req.setLastName("User");

        LoginResponse resp = loginService.register(req);
        assertFalse(resp.isSuccess());
        assertEquals("Email ya registrado", resp.getMessage());
    }

    // Este test valida que el servicio liste solo los usuarios activos.
    @Test
    void getAllUsers_returnsOnlyActiveUsers() {
        List<User> users = loginService.getAllUsers();
        assertEquals(1, users.size());
        assertTrue(Boolean.TRUE.equals(users.get(0).getIsActive()));
    }

    // Este test comprueba que actualizar un usuario cambie sus datos principales.
    @Test
    void updateUser_changesFields() {
        RegisterRequest req = new RegisterRequest();
        req.setFirstName("Updated");
        req.setLastName("Name");
        req.setPhoneNumber("123456");
        req.setAddress("Calle 2");
        req.setCity("Arequipa");
        req.setPostalCode("04001");
        req.setPassword("newpass123");

        LoginResponse resp = loginService.updateUser(1L, req);
        assertTrue(resp.isSuccess());
        assertEquals("Usuario actualizado", resp.getMessage());
        assertEquals("Updated", resp.getFirstName());
    }

    // Este test valida que eliminar un usuario lo marque como inactivo sin borrarlo físicamente.
    @Test
    void deleteUser_marksInactive() {
        LoginResponse resp = loginService.deleteUser(1L);
        assertTrue(resp.isSuccess());
        assertEquals("Usuario eliminado", resp.getMessage());
        assertFalse(store.stream().filter(user -> user.getId().equals(1L)).findFirst().orElseThrow().getIsActive());
    }

    // Este test comprueba la verificación de email existente desde el repositorio.
    @Test
    void emailExists_returnsTrueForExistingEmail() {
        assertTrue(loginService.emailExists("test@example.com"));
    }

    // Este test comprueba la verificación de email inexistente desde el repositorio.
    @Test
    void emailExists_returnsFalseForMissingEmail() {
        assertFalse(loginService.emailExists("unknown@example.com"));
    }
}

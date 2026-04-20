package com.back.zapateria.service;

import com.back.zapateria.dto.LoginRequest;
import com.back.zapateria.dto.LoginResponse;
import com.back.zapateria.dto.RegisterRequest;
import com.back.zapateria.model.User;
import com.back.zapateria.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ================= LOGIN =================
    public LoginResponse authenticate(LoginRequest req) {

        Optional<User> userOpt = userRepository.findByEmail(req.getEmail());

        if (userOpt.isEmpty())
            return fail("Usuario no encontrado");

        User user = userOpt.get();

        if (!user.getIsActive())
            return fail("Usuario inactivo");

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            return fail("Contraseña incorrecta");

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                "Login exitoso",
                true
        );
    }

    // ================= REGISTER =================
    public LoginResponse register(RegisterRequest req) {

        if (userRepository.existsByEmail(req.getEmail()))
            return fail("Email ya registrado");

        User u = new User();
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setPhoneNumber(req.getPhoneNumber());
        u.setAddress(req.getAddress());
        u.setCity(req.getCity());
        u.setPostalCode(req.getPostalCode());
        u.setRole("USER");
        u.setIsActive(true);

        User saved = userRepository.save(u);

        return new LoginResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getRole(),
                "Usuario registrado",
                true
        );
    }

    // ================= LIST USERS =================
    public List<User> getAllUsers() {
        return userRepository.findByIsActiveTrue();
    }

    // ================= UPDATE USER =================
    public LoginResponse updateUser(Long id, RegisterRequest req) {

        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return fail("Usuario no encontrado");

        User u = opt.get();

        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setPhoneNumber(req.getPhoneNumber());
        u.setAddress(req.getAddress());
        u.setCity(req.getCity());
        u.setPostalCode(req.getPostalCode());

        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            u.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        userRepository.save(u);

        return success(u, "Usuario actualizado");
    }

    // ================= DELETE (SOFT) =================
    public LoginResponse deleteUser(Long id) {

        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return fail("Usuario no encontrado");

        User u = opt.get();
        u.setIsActive(false);

        userRepository.save(u);

        return success(u, "Usuario eliminado");
    }

    // ================= EMAIL CHECK =================
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // ================= HELPERS =================
    private LoginResponse fail(String msg) {
        return new LoginResponse(null, null, null, null, null, msg, false);
    }

    private LoginResponse success(User u, String msg) {
        return new LoginResponse(
                u.getId(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getRole(),
                msg,
                true
        );
    }
}
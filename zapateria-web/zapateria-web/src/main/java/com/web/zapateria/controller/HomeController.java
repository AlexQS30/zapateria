package com.web.zapateria.controller;

import com.web.zapateria.service.ShoeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private ShoeService shoeService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("titulo", "FootStyle - Calzado y Accesorios");
        model.addAttribute("categories", shoeService.getCategories());
        model.addAttribute("featuredProducts", shoeService.getFeaturedProducts());
        return "index";
    }

    @GetMapping("/hombre")
    public String hombre(Model model) {
        model.addAttribute("titulo", "Calzados Hombre - FootStyle");
        model.addAttribute("categoria", "hombre");
        model.addAttribute("products", shoeService.getCategoryProducts("hombre"));
        return "categoria";
    }

    @GetMapping("/mujer")
    public String mujer(Model model) {
        model.addAttribute("titulo", "Calzados Mujer - FootStyle");
        model.addAttribute("categoria", "mujer");
        model.addAttribute("products", shoeService.getCategoryProducts("mujer"));
        return "categoria";
    }

    @GetMapping("/ninos")
    public String ninos(Model model) {
        model.addAttribute("titulo", "Calzados Niños - FootStyle");
        model.addAttribute("categoria", "ninos");
        model.addAttribute("products", shoeService.getCategoryProducts("ninos"));
        return "categoria";
    }

    @GetMapping("/accesorios")
    public String accesorios(Model model) {
        model.addAttribute("titulo", "Accesorios - FootStyle");
        model.addAttribute("categoria", "accesorios");
        model.addAttribute("products", shoeService.getCategoryProducts("accesorios"));
        return "categoria";
    }

    @GetMapping("/categoria")
    public String categoriaQuery(@RequestParam(value = "categoria", defaultValue = "") String categoria) {
        if (categoria == null) return "redirect:/";
        String c = categoria.trim().toLowerCase();
        switch (c) {
            case "hombre": return "redirect:/hombre";
            case "mujer": return "redirect:/mujer";
            case "niños":
            case "ninos": return "redirect:/ninos";
            case "accesorios": return "redirect:/accesorios";
            default:
                // fallback: redirect to home
                return "redirect:/";
        }
    }

    @GetMapping("/producto")
    public String producto(@RequestParam(value = "id", defaultValue = "1") String id, Model model) {
        model.addAttribute("titulo", "Detalle de Producto - FootStyle");
        model.addAttribute("product", shoeService.getProductDetail(id));
        return "producto";
    }

    @GetMapping("/contacto")
    public String contacto(Model model) {
        model.addAttribute("titulo", "Contacto - FootStyle");
        return "contacto";
    }

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        if (session.getAttribute("usuario") != null) {
            return "redirect:/";
        }
        model.addAttribute("titulo", "Ingresa a tu cuenta - FootStyle");
        return "login";
    }

    @GetMapping("/register")
    public String register(HttpSession session, Model model) {
        if (session.getAttribute("usuario") != null) {
            return "redirect:/";
        }
        model.addAttribute("titulo", "Crea tu cuenta - FootStyle");
        return "register";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email, @RequestParam String password, 
                                HttpSession session, Model model) {
        // NOTA: El login ahora se realiza a través de la API REST en zapateria-back
        // Esta función está aquí para compatibilidad con formularios tradicionales
        // Los datos se guardan en sessionStorage desde JavaScript
        
        // Para usar esta función, el cliente debe enviar los datos a /api/auth/login
        // Consulta: auth-integration.js para ver cómo se implementa
        
        model.addAttribute("error", "Por favor usa el formulario de login que consume la API REST");
        model.addAttribute("titulo", "Ingresa a tu cuenta - FootStyle");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("titulo", "Panel de Administración - FootStyle");
        return "admin/dashboard";
    }
}


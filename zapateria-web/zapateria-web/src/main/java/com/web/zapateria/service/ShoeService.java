package com.web.zapateria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.zapateria.client.BackendClient;
import com.web.zapateria.model.Category;
import com.web.zapateria.model.Product;

@Service
public class ShoeService {

    @Autowired(required = false)
    private BackendClient backendClient;

    public List<Product> getFeaturedProducts() {
        if (backendClient != null) {
            List<Product> offers = backendClient.getOffers();
            if (!offers.isEmpty()) {
                return offers;
            }
            List<Product> products = backendClient.getAllProducts();
            if (!products.isEmpty()) {
                return products.stream()
                        .filter(product -> product.getDiscount() > 0)
                        .toList();
            }
        }

        return List.of();
    }

    public List<Category> getCategories() {
        if (backendClient != null) {
            List<Category> categories = backendClient.getCategories();
            if (!categories.isEmpty()) {
                return categories;
            }
        }

        return List.of();
    }

    /**
     * Obtiene productos por categoría desde el backend
     * Mapea nombres en español a los nombres de categoría del backend
     */
    public List<Product> getCategoryProducts(String category) {
        String backendCategory = mapCategoryName(category);
        if (backendClient != null) {
            return backendClient.getProductsByCategory(backendCategory);
        }

        return List.of();
    }

    /**
     * Mapea nombres de categoría en minúscula a los nombres del backend
     */
    private String mapCategoryName(String category) {
        if (category == null) return "Hombre";

        return switch (category.toLowerCase()) {
            case "hombre" -> "Hombre";
            case "mujer" -> "Mujer";
            case "ninos", "niños" -> "Deportivos";
            case "deportivos" -> "Deportivos";
            case "formales" -> "Formales";
            case "accesorios" -> "Accesorios";
            default -> "Hombre";
        };
    }

    public Product getProductDetail(String id) {
        if (backendClient != null) {
            return backendClient.getProductDetail(id);
        }

        return null;
    }
}

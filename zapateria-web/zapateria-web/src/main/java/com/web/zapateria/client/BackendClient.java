package com.web.zapateria.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.web.zapateria.model.Category;
import com.web.zapateria.model.Product;

@Component
public class BackendClient {

    @Value("${backend.api.url:http://localhost:8081/api}")
    private String backendApiUrl;

    private final RestTemplate restTemplate;

    public BackendClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Obtiene todos los productos del backend
     */
    public List<Product> getAllProducts() {
        try {
            String url = backendApiUrl + "/shoes";
            Object response = restTemplate.getForObject(url, List.class);
            return mapProducts(response);
        } catch (RestClientException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtiene productos por categoría desde el backend
     */
    public List<Product> getProductsByCategory(String category) {
        try {
            String url = backendApiUrl + "/shoes/category/{category}";
            Object response = restTemplate.getForObject(url, List.class, category);
            return mapProducts(response);
        } catch (RestClientException e) {
            System.err.println("Error al obtener productos de categoría " + category + ": " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Busca productos por nombre
     */
    public List<Product> searchByName(String query) {
        try {
            String url = backendApiUrl + "/shoes?q={query}";
            Object response = restTemplate.getForObject(url, List.class, query);
            return mapProducts(response);
        } catch (RestClientException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtiene ofertas (productos con descuento)
     */
    public List<Product> getOffers() {
        try {
            String url = backendApiUrl + "/shoes/offers";
            Object response = restTemplate.getForObject(url, List.class);
            return mapProducts(response);
        } catch (RestClientException e) {
            System.err.println("Error al obtener ofertas: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtiene las categorías del backend
     */
    public List<Category> getCategories() {
        try {
            String url = backendApiUrl + "/categories";
            Object response = restTemplate.getForObject(url, List.class);
            return mapCategories(response);
        } catch (RestClientException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtiene detalles de un producto
     */
    public Product getProductDetail(String id) {
        try {
            String url = backendApiUrl + "/shoes/{id}";
            return restTemplate.getForObject(url, Product.class, id);
        } catch (RestClientException e) {
            System.err.println("Error al obtener detalle del producto: " + e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<Product> mapProducts(Object response) {
        if (!(response instanceof List<?> products) || products.isEmpty()) {
            return List.of();
        }

        List<Product> mappedProducts = new java.util.ArrayList<>();
        for (Object item : products) {
            if (item instanceof Map<?, ?> map) {
                mappedProducts.add(mapProduct((Map<String, Object>) map));
            }
        }
        return mappedProducts;
    }

    @SuppressWarnings("unchecked")
    private List<Category> mapCategories(Object response) {
        if (!(response instanceof List<?> categories) || categories.isEmpty()) {
            return List.of();
        }

        List<Category> mappedCategories = new java.util.ArrayList<>();
        for (Object item : categories) {
            if (item instanceof Map<?, ?> map) {
                mappedCategories.add(mapCategory((Map<String, Object>) map));
            }
        }
        return mappedCategories;
    }

    private Product mapProduct(Map<String, Object> productMap) {
        if (productMap == null) {
            return null;
        }

        String id = stringValue(productMap.get("id"));
        String name = stringValue(productMap.get("name"));
        double price = numberValue(productMap.get("price"));
        String imageUrl = stringValue(productMap.get("image"));
        int discount = (int) numberValue(productMap.get("discount"));
        boolean isNew = booleanValue(productMap.get("new"));
        double rating = numberValue(productMap.get("rating"));
        int reviews = (int) numberValue(productMap.getOrDefault("reviews", 0));

        Object category = productMap.get("category");
        String categoryName = category instanceof Map
                ? stringValue(((Map<?, ?>) category).get("name"))
                : stringValue(category);

        return new Product(id, name, null, price, imageUrl, categoryName, discount, isNew, rating, reviews);
    }

    private Category mapCategory(Map<String, Object> categoryMap) {
        if (categoryMap == null) {
            return null;
        }

        String id = stringValue(categoryMap.get("id"));
        String name = stringValue(categoryMap.get("name"));
        String imageUrl = stringValue(categoryMap.get("image"));
        String description = defaultCategoryDescription(name);

        return new Category(id, name, description, imageUrl);
    }

    private String stringValue(Object value) {
        return value != null ? value.toString() : null;
    }

    private double numberValue(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        return 0.0;
    }

    private boolean booleanValue(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        return value != null && Boolean.parseBoolean(value.toString());
    }

    private String defaultCategoryDescription(String name) {
        if (name == null) {
            return "Descubre nuestra colección completa";
        }

        return switch (name.toLowerCase()) {
            case "hombre" -> "Zapatos elegantes y cómodos";
            case "mujer" -> "Estilo y comodidad en cada paso";
            case "deportivos" -> "Calzado seguro y versátil para el día a día";
            case "formales" -> "Modelos sobrios para ocasiones especiales";
            case "accesorios" -> "Complementos para cuidar tu calzado";
            default -> "Descubre nuestra colección completa";
        };
    }
}

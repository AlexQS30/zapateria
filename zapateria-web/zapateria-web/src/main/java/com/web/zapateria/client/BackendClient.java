package com.web.zapateria.client;

import com.web.zapateria.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

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
            Product[] products = restTemplate.getForObject(url, Product[].class);
            return products != null ? Arrays.asList(products) : List.of();
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
            Product[] products = restTemplate.getForObject(url, Product[].class, category);
            return products != null ? Arrays.asList(products) : List.of();
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
            Product[] products = restTemplate.getForObject(url, Product[].class, query);
            return products != null ? Arrays.asList(products) : List.of();
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
            Product[] products = restTemplate.getForObject(url, Product[].class);
            return products != null ? Arrays.asList(products) : List.of();
        } catch (RestClientException e) {
            System.err.println("Error al obtener ofertas: " + e.getMessage());
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
}

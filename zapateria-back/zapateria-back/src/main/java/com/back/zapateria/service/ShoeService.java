package com.back.zapateria.service;

import com.back.zapateria.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShoeService {

    private List<Product> products = new ArrayList<>();

    public ShoeService() {

        products.add(new Product("1", "Zapato Casual Premium", 189.00,
                "/img/casual.jpg", "hombre", false, 20, 0, 4.5));

        products.add(new Product("2", "Tenis Deportivo Elite", 259.00,
                "/img/deportivo.jpg", "hombre", true, 0, 20, 5));

        products.add(new Product("3", "Zapato Formal Ejecutivo", 279.00,
                "/img/formal.jpg", "hombre", false, 15, 10, 4.8));

        products.add(new Product("4", "Zapatilla Running", 199.00,
                "/img/running.jpg", "hombre", false, 0, 0, 4.3));
    }

    // 🔹 obtener por categoría
    public List<Product> getCategoryProducts(String category) {
        return products.stream()
                                .filter(p -> p.getCategory() != null && p.getCategory().getName() != null && p.getCategory().getName().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    // 🔹 detalle
    public Product getProductDetail(String id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

        // 🔹 obtener todos
        public List<Product> getAllProducts() {
                return new ArrayList<>(products);
        }

        // 🔹 crear
        public Product createProduct(Product product) {
                // generate simple id if not present
                if (product.getId() == null || product.getId().isBlank()) {
                        String newId = String.valueOf(products.size() + 1);
                        product.setId(newId);
                }
                products.add(product);
                return product;
        }

        // 🔹 actualizar
        public Product updateProduct(String id, Product update) {
                for (int i = 0; i < products.size(); i++) {
                        if (products.get(i).getId().equals(id)) {
                                Product existing = products.get(i);
                                // replace fields
                                existing.setName(update.getName());
                                existing.setPrice(update.getPrice());
                                existing.setImage(update.getImage());
                                existing.setCategory(update.getCategory());
                                existing.setStock(update.getStock());
                                existing.setNew(update.isNew());
                                existing.setDiscount(update.getDiscount());
                                existing.setRating(update.getRating());
                                return existing;
                        }
                }
                return null;
        }

        // 🔹 eliminar
        public boolean deleteProduct(String id) {
                return products.removeIf(p -> p.getId().equals(id));
        }

        // 🔹 búsqueda por nombre (contiene)
        public List<Product> searchByName(String q) {
                String lower = q == null ? "" : q.toLowerCase();
                return products.stream()
                                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(lower))
                                .collect(Collectors.toList());
        }
}
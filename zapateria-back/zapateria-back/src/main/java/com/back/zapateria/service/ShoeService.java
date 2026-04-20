package com.back.zapateria.service;

import com.back.zapateria.model.Category;
import com.back.zapateria.model.Product;
import com.back.zapateria.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShoeService {

    private final ProductRepository productRepository;
    private CategoryService categoryService;

    @Autowired
    public ShoeService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    // 🔹 obtener por categoría
    public List<Product> getCategoryProducts(String category) {
        if (category == null || category.isBlank()) {
            return new ArrayList<>();
        }
        return productRepository.findByCategory_NameIgnoreCase(category);
    }

    // 🔹 detalle
    public Product getProductDetail(String id) {
        return productRepository.findById(id).orElse(null);
    }

        // 🔹 obtener todos
        public List<Product> getAllProducts() {
                return productRepository.findAll();
        }

        // 🔹 crear
        public Product createProduct(Product product) {
                return productRepository.save(product);
        }

        // 🔹 actualizar
        public Product updateProduct(String id, Product update) {
                return productRepository.findById(id).map(existing -> {
                        existing.setName(update.getName());
                        existing.setPrice(update.getPrice());
                        existing.setImage(update.getImage());
                        existing.setCategory(update.getCategory());
                        existing.setStock(update.getStock());
                        existing.setNew(update.isNew());
                        existing.setDiscount(update.getDiscount());
                        existing.setRating(update.getRating());
                        return productRepository.save(existing);
                }).orElse(null);
        }

        // 🔹 eliminar
        public boolean deleteProduct(String id) {
                if (!productRepository.existsById(id)) {
                        return false;
                }
                productRepository.deleteById(id);
                return true;
        }

        // 🔹 búsqueda por nombre (contiene)
        public List<Product> searchByName(String q) {
                String lower = q == null ? "" : q.toLowerCase();
                if (lower.isBlank()) {
                        return productRepository.findAll();
                }
                return productRepository.findByNameContainingIgnoreCase(lower);
        }

        // 🔹 obtener productos por ID de categoría
        public List<Product> getProductsByCategoryId(Long categoryId) {
                if (categoryId == null) {
                        return new ArrayList<>();
                }
                return productRepository.findByCategory_Id(categoryId);
        }
}
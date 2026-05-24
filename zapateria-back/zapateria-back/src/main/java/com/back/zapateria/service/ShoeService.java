package com.back.zapateria.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.back.zapateria.model.Category;
import com.back.zapateria.model.Product;
import com.back.zapateria.model.ProductVariant;
import com.back.zapateria.repository.ProductRepository;
import com.back.zapateria.repository.ProductVariantRepository;

@Service
public class ShoeService {

    private final ProductRepository productRepository;
        private final ProductVariantRepository productVariantRepository;
    private CategoryService categoryService;

    @Autowired
        public ShoeService(ProductRepository productRepository, ProductVariantRepository productVariantRepository, CategoryService categoryService) {
                this.productRepository = productRepository;
                this.productVariantRepository = productVariantRepository;
                this.categoryService = categoryService;
        }

        /**
         * Backwards-compatible constructor used by tests or frameworks that supply only
         * ProductRepository and CategoryService. ProductVariantRepository will be null
         * in this case and methods handle its absence.
         */
        public ShoeService(ProductRepository productRepository, CategoryService categoryService) {
                this(productRepository, null, categoryService);
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

        public List<ProductVariant> getProductVariants(String productId) {
                if (productVariantRepository == null || productId == null || productId.isBlank()) {
                        return List.of();
                }
                return productVariantRepository.findByProduct_Id(productId);
        }

        public Map<String, Object> getAvailability(String productId) {
                Product product = getProductDetail(productId);
                List<ProductVariant> variants = getProductVariants(productId);

                Map<String, Object> response = new LinkedHashMap<>();
                response.put("productId", productId);
                response.put("name", product != null ? product.getName() : null);
                response.put("stock", product != null ? product.getStock() : 0);
                response.put("hasStock", product != null && product.getStock() > 0);
                response.put("variants", variants);
                return response;
        }

        // 🔹 obtener todos
        public List<Product> getAllProducts() {
                return productRepository.findAll();
        }

        // 🔹 crear
        public Product createProduct(Product product) {
                product.setCategory(resolveCategory(product.getCategory(), null));
                return productRepository.save(product);
        }

        // 🔹 actualizar
        public Product updateProduct(String id, Product update) {
                return productRepository.findById(id).map(existing -> {
                        existing.setName(update.getName());
                        existing.setPrice(update.getPrice());
                        existing.setImage(update.getImage());
                        existing.setCategory(resolveCategory(update.getCategory(), existing.getCategory()));
                        existing.setStock(update.getStock());
                        existing.setNew(update.isNew());
                        existing.setDiscount(update.getDiscount());
                        existing.setRating(update.getRating());
                                existing.setVariants(update.getVariants());
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

        private Category resolveCategory(Category incoming, Category fallback) {
                if (incoming == null) {
                        return fallback;
                }

                if (incoming.getId() != null && categoryService != null) {
                        return categoryService.getById(incoming.getId()).orElse(fallback);
                }

                if (incoming.getName() != null && !incoming.getName().isBlank() && categoryService != null) {
                        return categoryService.getOrCreateByName(incoming.getName().trim());
                }

                return fallback;
        }
}
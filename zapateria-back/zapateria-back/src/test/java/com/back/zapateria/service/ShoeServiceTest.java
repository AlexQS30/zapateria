package com.back.zapateria.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.back.zapateria.model.Category;
import com.back.zapateria.model.Product;
import com.back.zapateria.model.ProductVariant;
import com.back.zapateria.repository.ProductRepository;
import com.back.zapateria.repository.ProductVariantRepository;

class ShoeServiceTest {

    private ShoeService service;
    
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private CategoryService categoryService;

    private final List<Product> store = new ArrayList<>();
    private final List<ProductVariant> variantStore = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Category hombre = new Category("Hombre");
        hombre.setId(1L);
        Product seeded = new Product("1", "Zapato Casual Premium", 189.0, "/img/casual.jpg", hombre, false, 20, 0, 4.5);
        ProductVariant seededVariant = new ProductVariant(seeded, "39", "Negro", 5);
        seeded.addVariant(seededVariant);
        store.add(seeded);
        variantStore.add(seededVariant);

        Category mujer = new Category("Mujer");
        mujer.setId(2L);
        Category nino = new Category("Nino");
        nino.setId(3L);

        when(categoryService.getById(1L)).thenReturn(Optional.of(hombre));
        when(categoryService.getById(2L)).thenReturn(Optional.of(mujer));
        when(categoryService.getById(3L)).thenReturn(Optional.of(nino));
        when(categoryService.getById(999L)).thenReturn(Optional.empty());
        when(categoryService.getOrCreateByName(anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            Category category = new Category(name);
            category.setId(100L + store.size());
            return category;
        });

        when(productRepository.findAll()).thenAnswer(invocation -> new ArrayList<>(store));
        when(productRepository.findById(anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            return store.stream().filter(product -> product.getId().equals(id)).findFirst();
        });
        when(productRepository.existsById(anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            return store.stream().anyMatch(product -> product.getId().equals(id));
        });
        when(productRepository.findByNameContainingIgnoreCase(anyString())).thenAnswer(invocation -> {
            String query = invocation.getArgument(0).toString().toLowerCase();
            return store.stream()
                    .filter(product -> product.getName() != null && product.getName().toLowerCase().contains(query))
                    .toList();
        });
        when(productRepository.findByCategory_NameIgnoreCase(anyString())).thenAnswer(invocation -> {
            String categoryName = invocation.getArgument(0).toString().toLowerCase();
            return store.stream()
                    .filter(product -> product.getCategory() != null
                            && product.getCategory().getName() != null
                            && product.getCategory().getName().toLowerCase().equals(categoryName))
                    .toList();
        });
        when(productRepository.findByCategory_Id(anyLong())).thenAnswer(invocation -> {
            Long categoryId = invocation.getArgument(0);
            return store.stream()
                    .filter(product -> product.getCategoryId() != null && product.getCategoryId().equals(categoryId))
                    .toList();
        });
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            if (product.getId() == null || product.getId().isBlank()) {
                product.setId(String.valueOf(store.size() + 1));
            }
            store.removeIf(existing -> existing.getId().equals(product.getId()));
            store.add(product);
            return product;
        });
        doAnswer(invocation -> {
            String id = invocation.getArgument(0);
            store.removeIf(product -> product.getId().equals(id));
            variantStore.removeIf(variant -> variant.getProduct() != null && id.equals(variant.getProduct().getId()));
            return null;
        }).when(productRepository).deleteById(anyString());

        when(productVariantRepository.findByProduct_Id(anyString())).thenAnswer(invocation -> {
            String productId = invocation.getArgument(0);
            return variantStore.stream()
                    .filter(variant -> variant.getProduct() != null && productId.equals(variant.getProduct().getId()))
                    .toList();
        });

        service = new ShoeService(productRepository, productVariantRepository, categoryService);
    }

    // Este test verifica que el servicio devuelva todos los productos disponibles en el repositorio simulado.
    @Test
    void getAllProducts_notEmpty() {
        List<Product> all = service.getAllProducts();
        assertNotNull(all);
        assertFalse(all.isEmpty());
    }

    // Este test comprueba que al crear un producto nuevo el repositorio le asigne un ID automáticamente.
    @Test
    void createProduct_assignsId() {
        Product p = new Product(null, "Test Shoe", 10.0, "/img.png", "hombre", false, 5, 0, 0.0);
        Product created = service.createProduct(p);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Test Shoe", created.getName());
    }

    // Este test valida que actualizar un producto cambie su nombre, precio y bandera de novedad.
    @Test
    void updateProduct_changesFields() {
        Product p = new Product(null, "A", 1.0, "/a.png", "hombre", false, 5, 0, 0.0);
        Product created = service.createProduct(p);

        Product upd = new Product(null, "B", 2.0, "/b.png", "hombre", true, 3, 10, 4.0);
        Product result = service.updateProduct(created.getId(), upd);

        assertNotNull(result);
        assertEquals("B", result.getName());
        assertTrue(result.isNew());
    }

    // Este test comprueba que eliminar un producto lo quite del almacenamiento usado por el mock.
    @Test
    void deleteProduct_removes() {
        Product p = new Product(null, "ToDelete", 5.0, "/d.png", "hombre", false, 2, 0, 0.0);
        Product created = service.createProduct(p);
        boolean removed = service.deleteProduct(created.getId());
        assertTrue(removed);
        assertNull(service.getProductDetail(created.getId()));
    }

    // Este test valida que la búsqueda por nombre encuentre coincidencias parciales o exactas.
    @Test
    void searchByName_finds() {
        service.createProduct(new Product(null, "UniqueXYZ", 1.0, "/u.png", "hombre", false, 1, 0, 0.0));
        List<Product> res = service.searchByName("UniqueXYZ");
        assertNotNull(res);
        assertFalse(res.isEmpty());
    }

    // Este test comprueba que el filtro por ID de categoría devuelva solo los productos asociados a esa categoría.
    @Test
    void getProductsByCategoryId_filtersByCategoryId() {
        Category category = new Category("Mujer");
        category.setId(2L);
        service.createProduct(new Product(null, "Category Match", 9.0, "/x.png", category, false, 1, 0, 4.0));

        List<Product> res = service.getProductsByCategoryId(2L);
        assertFalse(res.isEmpty());
        assertEquals(2L, res.get(res.size() - 1).getCategoryId());
    }

    // Este test valida que si una categoría no tiene productos, el servicio responda con una lista vacía.
    @Test
    void getProductsByCategoryId_returnsEmptyWhenNoMatch() {
        List<Product> res = service.getProductsByCategoryId(999L);
        assertTrue(res.isEmpty());
    }

    @Test
    void getProductVariants_returnsVariantsByProductId() {
        List<ProductVariant> variants = service.getProductVariants("1");
        assertEquals(1, variants.size());
        assertEquals("39", variants.get(0).getSize());
        assertEquals("Negro", variants.get(0).getColor());
    }

    @Test
    void getAvailability_includesStockAndVariants() {
        Map<String, Object> availability = service.getAvailability("1");

        assertEquals("1", availability.get("productId"));
        assertEquals("Zapato Casual Premium", availability.get("name"));
        assertEquals(20, availability.get("stock"));
        assertEquals(true, availability.get("hasStock"));
        assertTrue(availability.containsKey("variants"));
    }

    @Test
    void backwardCompatibleConstructor_handlesMissingVariantRepository() {
        ShoeService legacyService = new ShoeService(productRepository, categoryService);
        assertTrue(legacyService.getProductVariants("1").isEmpty());
    }

    @Test
    void createProduct_resolvesAndPersistsCategoryById() {
        Product input = new Product(null, "Con categoria", 15.0, "/c.png", (Category) null, false, 3, 0, 4.0);
        input.setCategoryId(2L);

        Product created = service.createProduct(input);

        assertNotNull(created.getCategory());
        assertEquals(2L, created.getCategoryId());
        assertEquals("Mujer", created.getCategory().getName());
    }

    @Test
    void updateProduct_changesCategoryById() {
        Product existing = service.createProduct(new Product(null, "Cambiar categoria", 20.0, "/p.png", "Hombre", false, 2, 0, 4.0));

        Product update = new Product(null, "Cambiar categoria", 20.0, "/p.png", (Category) null, false, 2, 0, 4.0);
        update.setCategoryId(3L);

        Product result = service.updateProduct(existing.getId(), update);

        assertNotNull(result);
        assertEquals(3L, result.getCategoryId());
        assertEquals("Nino", result.getCategory().getName());
    }
}

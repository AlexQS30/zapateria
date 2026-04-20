package com.back.zapateria.service;

import com.back.zapateria.model.Category;
import com.back.zapateria.model.Product;
import com.back.zapateria.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;

class ShoeServiceTest {

    private ShoeService service;
    
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    private final List<Product> store = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Category hombre = new Category("Hombre");
        hombre.setId(1L);
        store.add(new Product("1", "Zapato Casual Premium", 189.0, "/img/casual.jpg", hombre, false, 20, 0, 4.5));

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
            return null;
        }).when(productRepository).deleteById(anyString());

        service = new ShoeService(productRepository, categoryService);
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
}

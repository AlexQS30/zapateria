package com.back.zapateria.service;

import com.back.zapateria.model.Category;
import com.back.zapateria.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private final List<Category> store = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService();
        try {
            var field = CategoryService.class.getDeclaredField("categoryRepository");
            field.setAccessible(true);
            field.set(categoryService, categoryRepository);
        } catch (Exception ignored) {}

        Category hombre = new Category("Hombre");
        hombre.setId(1L);
        store.add(hombre);

        when(categoryRepository.findAll()).thenAnswer(invocation -> new ArrayList<>(store));
        when(categoryRepository.findById(any())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return store.stream().filter(category -> category.getId().equals(id)).findFirst();
        });
        when(categoryRepository.findByNameIgnoreCase(anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0).toString().toLowerCase();
            return store.stream()
                    .filter(category -> category.getName() != null && category.getName().toLowerCase().equals(name))
                    .findFirst();
        });
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            if (category.getId() == null) {
                category.setId((long) (store.size() + 1));
            }
            store.removeIf(existing -> existing.getId().equals(category.getId()));
            store.add(category);
            return category;
        });
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            store.removeIf(existing -> existing.getId().equals(id));
            return null;
        }).when(categoryRepository).deleteById(anyLong());
    }

    // Este test verifica que el servicio devuelva todas las categorías que existen en el repositorio.
    @Test
    void getAll_returnsList() {
        var res = categoryService.getAll();
        assertNotNull(res);
        assertFalse(res.isEmpty());
    }

    // Este test comprueba que buscar una categoría por nombre ignore mayúsculas y minúsculas.
    @Test
    void getByName_findsCategoryIgnoringCase() {
        Optional<Category> category = categoryService.getByName("hombre");
        assertTrue(category.isPresent());
        assertEquals("Hombre", category.get().getName());
    }

    // Este test valida que el servicio reutiliza una categoría existente en lugar de duplicarla.
    @Test
    void getOrCreateByName_returnsExistingCategory() {
        Category category = categoryService.getOrCreateByName("Hombre");
        assertNotNull(category);
        assertEquals(1L, category.getId());
    }

    // Este test comprueba que el servicio crea una categoría nueva cuando todavía no existe en la base.
    @Test
    void getOrCreateByName_createsNewCategory() {
        Category created = categoryService.getOrCreateByName("Accesorios");
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Accesorios", created.getName());
    }

    // Este test valida que eliminar una categoría la saca del repositorio simulado.
    @Test
    void delete_removesCategory() {
        categoryService.delete(1L);
        assertTrue(categoryService.getById(1L).isEmpty());
    }
 
}

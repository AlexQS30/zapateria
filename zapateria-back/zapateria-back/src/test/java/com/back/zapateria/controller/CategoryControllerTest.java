package com.back.zapateria.controller;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.back.zapateria.model.Category;
import com.back.zapateria.service.CategoryService;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController controller;

    @Test
    void list_returnsAllCategories() {
        when(categoryService.getAll()).thenReturn(List.of(new Category("Hombre"), new Category("Mujer")));

        ResponseEntity<List<Category>> response = controller.list();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(categoryService).getAll();
    }

    @Test
    void create_returnsCreatedCategory() {
        Category input = new Category("Deportivo");
        Category created = new Category("Deportivo");
        created.setId(7L);
        when(categoryService.create(input)).thenReturn(created);

        ResponseEntity<Category> response = controller.create(input);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(7L, response.getBody().getId());
    }

    @Test
    void getOne_foundAndNotFound_paths() {
        Category found = new Category("Casual");
        found.setId(1L);
        when(categoryService.getById(1L)).thenReturn(Optional.of(found));
        when(categoryService.getById(2L)).thenReturn(Optional.empty());

        ResponseEntity<Category> foundResponse = controller.getOne(1L);
        ResponseEntity<Category> notFoundResponse = controller.getOne(2L);

        assertEquals(200, foundResponse.getStatusCode().value());
        assertEquals("Casual", foundResponse.getBody().getName());
        assertEquals(404, notFoundResponse.getStatusCode().value());
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(5L);

        assertEquals(204, response.getStatusCode().value());
        verify(categoryService).delete(5L);
    }
}

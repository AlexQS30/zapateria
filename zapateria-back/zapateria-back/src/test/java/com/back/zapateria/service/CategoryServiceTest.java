package com.back.zapateria.service;

import com.back.zapateria.model.Category;
import com.back.zapateria.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService();
        // inject mock manually
        try {
            var field = CategoryService.class.getDeclaredField("categoryRepository");
            field.setAccessible(true);
            field.set(categoryService, categoryRepository);
        } catch (Exception ignored) {}
    }

    @Test
    void getAll_returnsList() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of(new Category("Hombre")));

        // Act
        var res = categoryService.getAll();

        // Assert
        assertNotNull(res);
        assertFalse(res.isEmpty());
    }
 
}

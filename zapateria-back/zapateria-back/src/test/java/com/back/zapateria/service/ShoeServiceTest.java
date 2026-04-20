package com.back.zapateria.service;

import com.back.zapateria.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShoeServiceTest {

    private ShoeService service;

    @BeforeEach
    void setUp() {
        service = new ShoeService();
    }

    @Test
    void getAllProducts_notEmpty() {
        List<Product> all = service.getAllProducts();
        assertNotNull(all);
        assertFalse(all.isEmpty());
    }

    @Test
    void createProduct_assignsId() {
        Product p = new Product(null, "Test Shoe", 10.0, "/img.png", "hombre", false, 5, 0, 0.0);
        Product created = service.createProduct(p);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Test Shoe", created.getName());
    }

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

    @Test
    void deleteProduct_removes() {
        Product p = new Product(null, "ToDelete", 5.0, "/d.png", "hombre", false, 2, 0, 0.0);
        Product created = service.createProduct(p);
        boolean removed = service.deleteProduct(created.getId());
        assertTrue(removed);
        assertNull(service.getProductDetail(created.getId()));
    }

    @Test
    void searchByName_finds() {
        service.createProduct(new Product(null, "UniqueXYZ", 1.0, "/u.png", "hombre", false, 1, 0, 0.0));
        List<Product> res = service.searchByName("UniqueXYZ");
        assertNotNull(res);
        assertFalse(res.isEmpty());
    }
}

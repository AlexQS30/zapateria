package com.back.zapateria.controller;

import com.back.zapateria.model.Product;
import com.back.zapateria.service.ShoeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoeControllerTest {

    @Mock
    private ShoeService shoeService;

    @InjectMocks
    private ShoeController controller;

    @Test
    void listAll_withQuery_callsSearch() {
        when(shoeService.searchByName("q")).thenReturn(List.of(new Product("10", "Qshoe", 1.0, "/i", "hombre", false, 2, 0, 0.0)));

        ResponseEntity<List<Product>> resp = controller.listAll("q");

        assertEquals(200, resp.getStatusCode().value());
        assertNotNull(resp.getBody());
        assertEquals(1, resp.getBody().size());
        verify(shoeService).searchByName("q");
    }

    @Test
    void listAll_withoutQuery_returnsAll() {
        List<Product> allProducts = List.of(
            new Product("1", "Shoe1", 100.0, "/i1", "hombre", false, 5, 0, 4.5),
            new Product("2", "Shoe2", 200.0, "/i2", "mujer", true, 10, 15, 5.0)
        );
        when(shoeService.getAllProducts()).thenReturn(allProducts);

        ResponseEntity<List<Product>> resp = controller.listAll(null);

        assertEquals(200, resp.getStatusCode().value());
        assertNotNull(resp.getBody());
        assertEquals(2, resp.getBody().size());
        verify(shoeService).getAllProducts();
    }

    @Test
    void byCategory_returnsProductsForCategory() {
        List<Product> categoryProducts = List.of(
            new Product("1", "HombresShoe1", 100.0, "/i1", "Hombre", false, 5, 0, 4.5),
            new Product("2", "HombresShoe2", 150.0, "/i2", "Hombre", true, 10, 20, 5.0)
        );
        when(shoeService.getCategoryProducts("Hombre")).thenReturn(categoryProducts);

        ResponseEntity<List<Product>> resp = controller.byCategory("Hombre");

        assertEquals(200, resp.getStatusCode().value());
        assertNotNull(resp.getBody());
        assertEquals(2, resp.getBody().size());
        verify(shoeService).getCategoryProducts("Hombre");
    }

    @Test
    void byCategory_emptyResultForNonexistentCategory() {
        when(shoeService.getCategoryProducts("NoExiste")).thenReturn(List.of());

        ResponseEntity<List<Product>> resp = controller.byCategory("NoExiste");

        assertEquals(200, resp.getStatusCode().value());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().isEmpty());
        verify(shoeService).getCategoryProducts("NoExiste");
    }

    @Test
    void getOne_foundAndNotFound() {
        Product p = new Product("1", "Found", 1.0, "/i", "hombre", false, 3, 0, 0.0);
        when(shoeService.getProductDetail("1")).thenReturn(p);
        ResponseEntity<Product> r1 = controller.getOne("1");
        assertEquals(200, r1.getStatusCode().value());
        assertEquals(p, r1.getBody());

        when(shoeService.getProductDetail("2")).thenReturn(null);
        ResponseEntity<Product> r2 = controller.getOne("2");
        assertEquals(404, r2.getStatusCode().value());
    }

    @Test
    void create_returnsCreated() {
        Product p = new Product(null, "New", 2.0, "/i", "hombre", false, 4, 0, 0.0);
        Product created = new Product("99", "New", 2.0, "/i", "hombre", false, 4, 0, 0.0);
        when(shoeService.createProduct(p)).thenReturn(created);

        ResponseEntity<Product> resp = controller.create(p);
        assertEquals(201, resp.getStatusCode().value());
        assertEquals(created, resp.getBody());
    }

    @Test
    void update_and_delete_behaviour() {
        Product upd = new Product(null, "U", 3.0, "/i", "hombre", false, 2, 0, 0.0);
        Product updated = new Product("5", "U", 3.0, "/i", "hombre", false, 2, 0, 0.0);
        when(shoeService.updateProduct("5", upd)).thenReturn(updated);

        ResponseEntity<Product> r = controller.update("5", upd);
        assertEquals(200, r.getStatusCode().value());
        assertEquals(updated, r.getBody());

        when(shoeService.updateProduct("6", upd)).thenReturn(null);
        ResponseEntity<Product> r2 = controller.update("6", upd);
        assertEquals(404, r2.getStatusCode().value());

        when(shoeService.deleteProduct("5")).thenReturn(true);
        ResponseEntity<Void> d1 = controller.delete("5");
        assertEquals(204, d1.getStatusCode().value());

        when(shoeService.deleteProduct("6")).thenReturn(false);
        ResponseEntity<Void> d2 = controller.delete("6");
        assertEquals(404, d2.getStatusCode().value());
    }
}

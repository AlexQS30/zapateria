package com.back.zapateria.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

class ProductPhotoTest {

    @Test
    void constructor_initializesFields() {
        Product product = new Product("p1", "Sneaker", 100.0, "/img.jpg", "Hombre", true, 10, 0, 4.5);

        ProductPhoto photo = new ProductPhoto(product, "/detail.jpg", 3);

        assertSame(product, photo.getProduct());
        assertEquals("/detail.jpg", photo.getImageUrl());
        assertEquals(3, photo.getSortOrder());
    }

    @Test
    void setters_and_getters_workForAllFields() {
        ProductPhoto photo = new ProductPhoto();
        Product product = new Product("p2", "Street", 120.0, "/img2.jpg", "Mujer", false, 5, 5, 4.0);

        photo.setId(12L);
        photo.setProduct(product);
        photo.setImageUrl("/photo.jpg");
        photo.setSortOrder(5);

        assertEquals(12L, photo.getId());
        assertSame(product, photo.getProduct());
        assertEquals("/photo.jpg", photo.getImageUrl());
        assertEquals(5, photo.getSortOrder());
    }

    @Test
    void defaultConstructor_keepsNullableFieldsNull() {
        ProductPhoto photo = new ProductPhoto();

        assertNull(photo.getId());
        assertNull(photo.getProduct());
        assertNull(photo.getImageUrl());
        assertNull(photo.getSortOrder());
    }
}

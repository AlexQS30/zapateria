package com.back.zapateria.model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void getGallery_mergesImageAndPhotos_distinctAndTrimmed() {
        Product product = new Product("p1", "Sneaker", 100.0, "/img-main.jpg", "Hombre", true, 10, 0, 4.5);
        product.addPhoto(new ProductPhoto(null, " /img-1.jpg ", 1));
        product.addPhoto(new ProductPhoto(null, "/img-1.jpg", 2));
        product.addPhoto(new ProductPhoto(null, "   ", 3));
        product.addPhoto(new ProductPhoto(null, null, 4));

        List<String> gallery = product.getGallery();

        assertEquals(2, gallery.size());
        assertTrue(gallery.contains("/img-main.jpg"));
        assertTrue(gallery.contains("/img-1.jpg"));
    }

    @Test
    void variantMethods_setBackReference_andManageCollection() {
        Product product = new Product("p1", "Sneaker", 100.0, "/img.jpg", "Hombre", true, 10, 0, 4.5);
        ProductVariant variant1 = new ProductVariant(null, "40", "Negro", 3);
        ProductVariant variant2 = new ProductVariant(null, "41", "Blanco", 4);

        product.addVariant(null);
        product.addVariant(variant1);
        product.setVariants(List.of(variant2));

        assertEquals(1, product.getVariants().size());
        assertSame(product, product.getVariants().get(0).getProduct());

        product.clearVariants();
        assertTrue(product.getVariants().isEmpty());
    }

    @Test
    void photoMethods_setBackReference_andManageCollection() {
        Product product = new Product("p1", "Sneaker", 100.0, "/img.jpg", "Hombre", true, 10, 0, 4.5);
        ProductPhoto photo1 = new ProductPhoto(null, "/a.jpg", 1);
        ProductPhoto photo2 = new ProductPhoto(null, "/b.jpg", 2);

        product.addPhoto(null);
        product.addPhoto(photo1);
        product.setPhotos(List.of(photo2));

        assertEquals(1, product.getPhotos().size());
        assertSame(product, product.getPhotos().get(0).getProduct());

        product.clearPhotos();
        assertTrue(product.getPhotos().isEmpty());
    }

    @Test
    void categoryId_returnsNullWithoutCategory_andValueWithCategory() {
        Product withoutCategory = new Product("p1", "Sneaker", 100.0, "/img.jpg", (Category) null, true, 10, 0, 4.5);
        assertNull(withoutCategory.getCategoryId());

        Category category = new Category("Urbano");
        category.setId(9L);
        Product withCategory = new Product("p2", "Street", 120.0, "/img2.jpg", category, false, 2, 10, 4.0);
        assertEquals(9L, withCategory.getCategoryId());
    }
}

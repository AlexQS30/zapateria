package com.back.zapateria.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class ProductJsonMappingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserialize_acceptsCategoryObjectWithId() throws Exception {
        String json = """
                {
                  \"id\": \"10\",
                  \"name\": \"Zapato Demo\",
                  \"price\": 199.9,
                  \"image\": \"/img/demo.jpg\",
                  \"category\": { \"id\": 1 },
                  \"stock\": 10,
                  \"discount\": 0,
                  \"rating\": 4.5
                }
                """;

        Product product = objectMapper.readValue(json, Product.class);

        assertNotNull(product.getCategory());
        assertEquals(1L, product.getCategoryId());
    }

    @Test
    void deserialize_acceptsCategoryIdField() throws Exception {
        String json = """
                {
                  \"id\": \"11\",
                  \"name\": \"Zapato Demo 2\",
                  \"price\": 149.9,
                  \"categoryId\": 2,
                  \"stock\": 5,
                  \"discount\": 5,
                  \"rating\": 4.0
                }
                """;

        Product product = objectMapper.readValue(json, Product.class);

        assertNotNull(product.getCategory());
        assertEquals(2L, product.getCategoryId());
    }
}

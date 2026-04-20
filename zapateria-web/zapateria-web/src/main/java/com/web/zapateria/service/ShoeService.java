package com.web.zapateria.service;

import com.web.zapateria.model.Product;
import com.web.zapateria.model.Category;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ShoeService {

    public List<Product> getFeaturedProducts() {
        return Arrays.asList(
            new Product("1", "Zapato Casual Premium", "Confortable para el día", 199.00,
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop", "hombre", 20, false, 4.5, 245),
            new Product("2", "Zapatilla Deportiva Elite", "Para actividades deportivas", 249.00,
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop", "hombre", 0, true, 5.0, 189),
            new Product("3", "Sandalia Cómoda de Cuero", "Perfecta para verano", 149.00,
                "https://images.unsplash.com/photo-1562183241-b937e341ade7?w=400&h=400&fit=crop", "mujer", 0, false, 4.0, 134),
            new Product("4", "Zapato Formal Ejecutivo", "Elegancia garantizada", 299.00,
                "https://images.unsplash.com/photo-1507222405253-b8ff5d6c0937?w=400&h=400&fit=crop", "hombre", 15, false, 4.8, 267)
        );
    }

    public List<Category> getCategories() {
        return Arrays.asList(
            new Category("hombre", "Calzados Hombre", "Zapatos elegantes y cómodos",
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=300&fit=crop"),
            new Category("mujer", "Calzados Mujer", "Estilo y comodidad en cada paso",
                "https://images.unsplash.com/photo-1543163521-9efcc06814ee?w=400&h=300&fit=crop"),
            new Category("ninos", "Calzados Niños", "Zapatos seguros y divertidos",
                "https://images.unsplash.com/photo-1541959227685-cdde63974b53?w=400&h=300&fit=crop"),
            new Category("accesorios", "Accesorios", "Complementos para tu estilo",
                "https://images.unsplash.com/photo-1572307480616-406f0ee9293e?w=400&h=300&fit=crop")
        );
    }

    public List<Product> getCategoryProducts(String category) {
        List<Product> products = new ArrayList<>();
        
        String[] names = {"Zapato Casual", "Tenis Deportivo", "Zapato Formal", "Zapatilla Running", 
                         "Zapato Elegante", "Oxford", "Sandalia", "Bota", "Tenis Casual", 
                         "Zapato Niño", "Zapato Escolar", "Tenis Colores"};
        
        String[] images = {
            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop",
            "https://images.unsplash.com/photo-1460353581641-694a62b78e76?w=400&h=400&fit=crop",
            "https://images.unsplash.com/photo-1507222405253-b8ff5d6c0937?w=400&h=400&fit=crop",
            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop",
            "https://images.unsplash.com/photo-1562183241-b937e341ade7?w=400&h=400&fit=crop",
            "https://images.unsplash.com/photo-1507222405253-b8ff5d6c0937?w=400&h=400&fit=crop",
            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop",
            "https://images.unsplash.com/photo-1541959227685-cdde63974b53?w=400&h=400&fit=crop",
            "https://images.unsplash.com/photo-1460353581641-694a62b78e76?w=400&h=400&fit=crop",
            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop",
            "https://images.unsplash.com/photo-1541959227685-cdde63974b53?w=400&h=400&fit=crop",
            "https://images.unsplash.com/photo-1460353581641-694a62b78e76?w=400&h=400&fit=crop"
        };
        
        double[] prices = {189, 249, 299, 199, 229, 279, 149, 319, 179, 99, 119, 159};
        
        for (int i = 0; i < 12; i++) {
            products.add(new Product(
                String.valueOf(i + 10), 
                names[i], 
                "Producto de calidad premium",
                prices[i],
                images[i],
                category,
                i % 3 == 0 ? 15 : 0,
                i % 4 == 0,
                3.5 + (i % 2) * 1.0,
                50 + i * 10
            ));
        }
        
        return products;
    }

    public Product getProductDetail(String id) {
        return new Product(
            id, 
            "Zapato Casual Premium", 
            "Zapato de excelente calidad, cómodo y elegante para cualquier ocasión",
            199.00,
            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&h=500&fit=crop",
            "hombre",
            20,
            false,
            4.5,
            245
        );
    }
}

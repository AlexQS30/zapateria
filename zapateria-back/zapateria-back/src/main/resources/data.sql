-- Categories
INSERT INTO category (id, name, image) VALUES
(1, 'Hombre', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=300&fit=crop'),
(2, 'Mujer', 'https://images.unsplash.com/photo-1543163521-9efcc06814ee?w=400&h=300&fit=crop'),
(3, 'Deportivos', 'https://images.unsplash.com/photo-1541959227685-cdde63974b53?w=400&h=300&fit=crop'),
(4, 'Formales', 'https://images.unsplash.com/photo-1507222405253-b8ff5d6c0937?w=400&h=300&fit=crop'),
(5, 'Accesorios', 'https://images.unsplash.com/photo-1572307480616-406f0ee9293e?w=400&h=300&fit=crop');

-- Products (id is string)
INSERT INTO product (id, name, price, image, category_id, stock, is_new, discount, rating) VALUES
('1','Zapato Casual Premium',189.00,'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop',1,20,false,20,4.5),
('2','Zapatilla Deportiva Elite',249.00,'https://images.unsplash.com/photo-1460353581641-694a62b78e76?w=400&h=400&fit=crop',3,15,true,0,5.0),
('3','Sandalia Cómoda de Cuero',149.00,'https://images.unsplash.com/photo-1562183241-b937e341ade7?w=400&h=400&fit=crop',2,18,false,0,4.0),
('4','Zapato Formal Ejecutivo',299.00,'https://images.unsplash.com/photo-1507222405253-b8ff5d6c0937?w=400&h=400&fit=crop',4,12,false,15,4.8),
('5','Derby Clásico',279.00,'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop',4,8,false,10,4.7),
('6','Kit de Limpieza Premium',79.00,'https://images.unsplash.com/photo-1572307480616-406f0ee9293e?w=400&h=400&fit=crop',5,50,true,0,4.1),
('7','Zapato Infantil Colorido',129.00,'https://images.unsplash.com/photo-1541959227685-cdde63974b53?w=400&h=400&fit=crop',3,25,true,5,4.3),
('8','Tenis Casual Urbano',179.00,'https://images.unsplash.com/photo-1460353581641-694a62b78e76?w=400&h=400&fit=crop',1,30,false,0,4.4);

-- Users ready for JWT login tests and Postman collection
-- admin@example.com / admin123
-- user@example.com / user123
-- test@example.com / test123
INSERT INTO users (id, email, password, first_name, last_name, phone_number, address, city, postal_code, created_at, updated_at, is_active, role)
VALUES (1,'admin@example.com','$2a$10$yKv/K9hKLPBcmJYSfuTJK.k2bKLQB7T/kImqGlGy5/z65euRNQJ9e','Admin','User','000','Av. Central 100','Lima','15001', now(), now(), true, 'ADMIN');

INSERT INTO users (id, email, password, first_name, last_name, phone_number, address, city, postal_code, created_at, updated_at, is_active, role)
VALUES (2,'user@example.com','$2a$10$eGB/rVGo81IpUHYEuxbyTuAz.7CBtiuiwf3pDzmIO6eSwu5rJ90.G','Regular','User','111','Calle 123','Lima','15002', now(), now(), true, 'USER');

INSERT INTO users (id, email, password, first_name, last_name, phone_number, address, city, postal_code, created_at, updated_at, is_active, role)
VALUES (3,'test@example.com','$2a$10$1gZyYWzkzx/PpxkgJc73jO0U4CskLE8huu7yTI71/ToFXTciWFIsy','Test','User','987654321','Calle 123','Lima','01001', now(), now(), true, 'USER');

-- Purchases: sample purchase by user 2 buying product 2
INSERT INTO purchase (id, user_id, total, created_at) VALUES ('p1','2',448.00, now());
INSERT INTO purchase_item (purchase_id, product_id, quantity) VALUES ('p1','2',1);
INSERT INTO purchase_item (purchase_id, product_id, quantity) VALUES ('p1','1',1);

-- Reviews: user 2 reviewed product 2, user 3 reviewed product 1
INSERT INTO review (product_id, user_id, rating, comment, created_at) VALUES ('2','2',5,'Excelente producto', now());
INSERT INTO review (product_id, user_id, rating, comment, created_at) VALUES ('1','3',4,'Muy comodos y de buena calidad', now());

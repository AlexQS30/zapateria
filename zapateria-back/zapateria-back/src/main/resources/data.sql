-- Script único de reinicio completo del catálogo y usuarios de prueba
-- Ejecutar sobre una base vacía o para reemplazar completamente los datos existentes

DELETE FROM purchase_item;
DELETE FROM review;
DELETE FROM purchase;
DELETE FROM product_photo;
DELETE FROM product;
DELETE FROM category;
DELETE FROM users;

INSERT INTO category (id, name, image) VALUES
(1, 'Hombre', 'https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg?w=400&h=300&fit=crop'),
(2, 'Mujer', 'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?w=400&h=300&fit=crop'),
(3, 'Deportivos', 'https://images.pexels.com/photos/1308881/pexels-photo-1308881.jpeg?w=400&h=300&fit=crop'),
(4, 'Formales', 'https://images.pexels.com/photos/267301/pexels-photo-267301.jpeg?w=400&h=300&fit=crop'),
(5, 'Accesorios', 'https://images.pexels.com/photos/298863/pexels-photo-298863.jpeg?w=400&h=300&fit=crop');

INSERT INTO product (id, name, price, image, category_id, stock, is_new, discount, rating) VALUES
('1','Zapato Casual Premium',189.00,'https://images.pexels.com/photos/1926769/pexels-photo-1926769.jpeg?w=400&h=400&fit=crop',1,20,false,20,4.5),
('2','Zapatilla Deportiva Elite',249.00,'https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg?w=400&h=400&fit=crop',3,15,true,0,5.0),
('3','Sandalia Cómoda de Cuero',149.00,'https://images.pexels.com/photos/1456706/pexels-photo-1456706.jpeg?w=400&h=400&fit=crop',2,18,false,0,4.0),
('4','Zapato Formal Ejecutivo',299.00,'https://images.pexels.com/photos/1926769/pexels-photo-1926769.jpeg?w=400&h=400&fit=crop',4,12,false,15,4.8),
('5','Derby Clásico',279.00,'https://images.pexels.com/photos/3932499/pexels-photo-3932499.jpeg?w=400&h=400&fit=crop',4,8,false,10,4.7),
('6','Kit de Limpieza Premium',79.00,'https://images.pexels.com/photos/2769274/pexels-photo-2769274.jpeg?w=400&h=400&fit=crop',5,50,true,0,4.1),
('7','Zapato Infantil Colorido',129.00,'https://images.pexels.com/photos/4112021/pexels-photo-4112021.jpeg?w=400&h=400&fit=crop',3,25,true,5,4.3),
('8','Tenis Casual Urbano',179.00,'https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg?w=400&h=400&fit=crop',1,30,false,0,4.4);

INSERT INTO product_variant (product_id, size, color, stock) VALUES
('1', '37', 'Negro', 5),
('1', '38', 'Negro', 5),
('1', '39', 'Marrón', 5),
('1', '40', 'Blanco', 5),
('2', '38', 'Azul', 4),
('2', '39', 'Negro', 5),
('2', '40', 'Blanco', 3),
('2', '41', 'Rojo', 3),
('3', '35', 'Marrón', 4),
('3', '36', 'Blanco', 4),
('3', '37', 'Marrón', 5),
('3', '38', 'Azul', 5),
('4', '38', 'Negro', 3),
('4', '39', 'Negro', 3),
('4', '40', 'Marrón', 3),
('4', '41', 'Negro', 3),
('5', '39', 'Negro', 4),
('5', '40', 'Marrón', 4),
('6', 'Único', 'Negro', 50),
('7', '28', 'Rojo', 8),
('7', '29', 'Azul', 7),
('7', '30', 'Negro', 5),
('7', '31', 'Blanco', 5),
('8', '40', 'Negro', 10),
('8', '41', 'Azul', 10),
('8', '42', 'Blanco', 10);

INSERT INTO product_photo (product_id, image_url, sort_order) VALUES
('1', 'https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg?w=600&h=600&fit=crop', 1),
('1', 'https://images.pexels.com/photos/267202/pexels-photo-267202.jpeg?w=600&h=600&fit=crop', 2),
('1', 'https://images.pexels.com/photos/6050909/pexels-photo-6050909.jpeg?w=600&h=600&fit=crop', 3),

('2', 'https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg?w=600&h=600&fit=crop', 1),
('2', 'https://images.pexels.com/photos/1308881/pexels-photo-1308881.jpeg?w=600&h=600&fit=crop', 2),
('2', 'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?w=600&h=600&fit=crop', 3),

('3', 'https://images.pexels.com/photos/1456706/pexels-photo-1456706.jpeg?w=600&h=600&fit=crop', 1),
('3', 'https://images.pexels.com/photos/19090/pexels-photo.jpg?w=600&h=600&fit=crop', 2),
('3', 'https://images.pexels.com/photos/19090/pexels-photo.jpg?w=600&h=600&fit=crop&sat=-40', 3),

('4', 'https://images.pexels.com/photos/267301/pexels-photo-267301.jpeg?w=600&h=600&fit=crop', 1),
('4', 'https://images.pexels.com/photos/298863/pexels-photo-298863.jpeg?w=600&h=600&fit=crop', 2),
('4', 'https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg?w=600&h=600&fit=crop&sat=-20', 3),

('5', 'https://images.pexels.com/photos/3932499/pexels-photo-3932499.jpeg?w=600&h=600&fit=crop', 1),
('5', 'https://images.pexels.com/photos/267202/pexels-photo-267202.jpeg?w=600&h=600&fit=crop&sat=-10', 2),
('5', 'https://images.pexels.com/photos/19090/pexels-photo.jpg?w=600&h=600&fit=crop&hue=15', 3),

('6', 'https://images.pexels.com/photos/2769274/pexels-photo-2769274.jpeg?w=600&h=600&fit=crop', 1),
('6', 'https://images.pexels.com/photos/298863/pexels-photo-298863.jpeg?w=600&h=600&fit=crop&hue=25', 2),
('6', 'https://images.pexels.com/photos/6050909/pexels-photo-6050909.jpeg?w=600&h=600&fit=crop&hue=-10', 3),

('7', 'https://images.pexels.com/photos/4112021/pexels-photo-4112021.jpeg?w=600&h=600&fit=crop', 1),
('7', 'https://images.pexels.com/photos/1308881/pexels-photo-1308881.jpeg?w=600&h=600&fit=crop&hue=12', 2),
('7', 'https://images.pexels.com/photos/1456706/pexels-photo-1456706.jpeg?w=600&h=600&fit=crop&hue=18', 3),

('8', 'https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg?w=600&h=600&fit=crop', 1),
('8', 'https://images.pexels.com/photos/1308881/pexels-photo-1308881.jpeg?w=600&h=600&fit=crop&hue=-8', 2),
('8', 'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?w=600&h=600&fit=crop&hue=-14', 3);

INSERT INTO users (id, email, password, first_name, last_name, phone_number, address, city, postal_code, created_at, updated_at, is_active, role) VALUES
(1,'admin@example.com','$2a$10$yKv/K9hKLPBcmJYSfuTJK.k2bKLQB7T/kImqGlGy5/z65euRNQJ9e','Admin','User','000','Av. Central 100','Lima','15001', now(), now(), true, 'ADMIN'),
(2,'user@example.com','$2a$10$eGB/rVGo81IpUHYEuxbyTuAz.7CBtiuiwf3pDzmIO6eSwu5rJ90.G','Regular','User','111','Calle 123','Lima','15002', now(), now(), true, 'USER'),
(3,'test@example.com','$2a$10$1gZyYWzkzx/PpxkgJc73jO0U4CskLE8huu7yTI71/ToFXTciWFIsy','Test','User','987654321','Calle 123','Lima','01001', now(), now(), true, 'USER');

INSERT INTO purchase (id, user_id, total, created_at) VALUES ('p1','2',448.00, now());
INSERT INTO purchase_item (purchase_id, product_id, quantity) VALUES ('p1','2',1);
INSERT INTO purchase_item (purchase_id, product_id, quantity) VALUES ('p1','1',1);

INSERT INTO review (product_id, user_id, rating, comment, created_at) VALUES ('2','2',5,'Excelente producto', now());
INSERT INTO review (product_id, user_id, rating, comment, created_at) VALUES ('1','3',4,'Muy comodos y de buena calidad', now());

SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT COALESCE(MAX(id), 1) FROM users));
